package vip.yazilim.p2g.web.service.p2g.impl.relation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.config.security.SecurityConfig;
import vip.yazilim.p2g.web.config.security.authority.AAuthorityProvider;
import vip.yazilim.p2g.web.constant.Privilege;
import vip.yazilim.p2g.web.constant.Role;
import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.entity.relation.RoomInvite;
import vip.yazilim.p2g.web.entity.relation.RoomUser;
import vip.yazilim.p2g.web.repository.relation.IRoomUserRepo;
import vip.yazilim.p2g.web.service.p2g.IRoomService;
import vip.yazilim.p2g.web.service.p2g.relation.IRoomInviteService;
import vip.yazilim.p2g.web.service.p2g.relation.IRoomUserService;
import vip.yazilim.p2g.web.util.DBHelper;
import vip.yazilim.spring.core.exception.general.InvalidArgumentException;
import vip.yazilim.spring.core.exception.general.InvalidUpdateException;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;
import vip.yazilim.spring.core.exception.general.database.DatabaseReadException;
import vip.yazilim.spring.core.exception.web.NotFoundException;
import vip.yazilim.spring.core.service.ACrudServiceImpl;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 2.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Transactional
@Service
public class RoomUserService extends ACrudServiceImpl<RoomUser, String> implements IRoomUserService {

    // static fields
    private Logger LOGGER = LoggerFactory.getLogger(RoomUserService.class);

    // injected dependencies
    @Autowired
    private IRoomUserRepo roomUserRepo;

    @Autowired
    private IRoomService roomService;

    @Autowired
    private IRoomInviteService roomInviteService;

    @Autowired
    private AAuthorityProvider authorityProvider;

    @Autowired
    private SecurityConfig securityConfig;

    @Override
    protected JpaRepository<RoomUser, String> getRepository() {
        return roomUserRepo;
    }

    @Override
    protected String getId(RoomUser entity) {
        return entity.getUuid();
    }

    @Override
    protected RoomUser preInsert(RoomUser entity) {
        entity.setUuid(DBHelper.getRandomUuid());
        return entity;
    }

    @Override
    public List<RoomUser> getRoomUsersByRoomUuid(String roomUuid) throws DatabaseException {
        try {
            return roomUserRepo.findRoomUserByRoomUuidOrderByUuid(roomUuid);
        } catch (Exception exception) {
            String errMsg = String.format("An error occurred while getting RoomUser with Room:[%s]", roomUuid);
            throw new DatabaseReadException(errMsg, exception);
        }
    }

    @Override
    public Optional<RoomUser> getRoomUser(String userUuid) throws DatabaseException {
        try {
            return roomUserRepo.findRoomUserByUserUuid(userUuid);
        } catch (Exception exception) {
            String errMsg = String.format("An error occurred while getting RoomUser with User:[%s]", userUuid);
            throw new DatabaseReadException(errMsg, exception);
        }
    }

    @Override
    public Optional<RoomUser> getRoomUser(String roomUuid, String userUuid) throws DatabaseException {
        try {
            return roomUserRepo.findRoomUserByRoomUuidAndUserUuid(roomUuid, userUuid);
        } catch (Exception exception) {
            String errMsg = String.format("An error occurred while getting RoomUser with Room:[%s], User:[%s]", roomUuid, userUuid);
            throw new DatabaseReadException(errMsg, exception);
        }
    }

    @Override
    public RoomUser joinRoom(String roomUuid, String userUuid, String password, Role role) throws DatabaseException, InvalidArgumentException {
        Optional<Room> roomOpt = roomService.getById(roomUuid);

        if (!roomOpt.isPresent()) {
            String err = String.format("Room[%s] can not found", roomUuid);
            throw new InvalidArgumentException(err);
        }

        Room room = roomOpt.get();
        RoomUser roomUser = new RoomUser();

        if (securityConfig.passwordEncoder().matches(password, room.getPassword())) {
            roomUser.setRoomUuid(roomUuid);
            roomUser.setUserUuid(userUuid);
            roomUser.setRoleName(role.getRoleName());
            roomUser.setActiveFlag(true);
        } else {
            throw new InvalidArgumentException("Wrong password.");
        }

        create(roomUser);

        return roomUser;
    }

    @Override
    public RoomUser joinRoomOwner(String roomUuid, String userUuid) throws DatabaseException, InvalidArgumentException {
        RoomUser roomUser = new RoomUser();

        roomUser.setRoomUuid(roomUuid);
        roomUser.setUserUuid(userUuid);
        roomUser.setRoleName(Role.ROOM_OWNER.getRoleName());
        roomUser.setActiveFlag(true);

        return create(roomUser);
    }

    @Override
    public RoomUser acceptRoomInvite(RoomInvite roomInvite) throws DatabaseException, InvalidArgumentException {
        if (roomInviteService.existsById(roomInvite.getUuid())) {
            RoomUser roomUser = new RoomUser();

            roomUser.setRoomUuid(roomInvite.getRoomUuid());
            roomUser.setUserUuid(roomInvite.getUserUuid());
            roomUser.setRoleName(Role.ROOM_USER.getRoleName());
            roomUser.setActiveFlag(true);

            return create(roomUser);
        } else {
            String err = String.format("Room Invite[%s] cannot found", roomInvite.getUuid());
            throw new NotFoundException(err);
        }
    }

    @Override
    public Role getRoleByRoomUuidAndUserUuid(String roomUuid, String userUuid) throws DatabaseException {
        Optional<RoomUser> roomUserOpt = getRoomUser(userUuid);

        if (!roomUserOpt.isPresent()) {
            return null;
        }

        String roleName = roomUserOpt.get().getRoleName();
        return Role.getRole(roleName);
    }

    @Override
    public boolean deleteRoomUsers(String roomUuid) throws DatabaseException {
        List<RoomUser> roomUserList = roomUserRepo.findRoomUserByRoomUuidOrderByUuid(roomUuid);

        for (RoomUser roomUser : roomUserList) {
            delete(roomUser);
        }

        return true;
    }

    @Override
    public List<Privilege> promoteUserRole(String roomUserUuid) throws DatabaseException, InvalidUpdateException, InvalidArgumentException {
        RoomUser roomUser = getSafeRoomUser(roomUserUuid);

        Role oldRole = Role.getRole(roomUser.getRoleName());
        Role newRole;

        switch (oldRole) {
            case ROOM_USER:
                newRole = Role.ROOM_MODERATOR;
                break;
            case ROOM_MODERATOR:
                newRole = Role.ROOM_ADMIN;
                break;
            default:
                newRole = oldRole;
                break;
        }

        roomUser.setRoleName(newRole.getRoleName());
        update(roomUser);

        return authorityProvider.getPrivilegeListByRoleName(newRole);
    }

    @Override
    public List<Privilege> demoteUserRole(String roomUserUuid) throws DatabaseException, InvalidUpdateException, InvalidArgumentException{
        RoomUser roomUser = getSafeRoomUser(roomUserUuid);

        Role oldRole = Role.getRole(roomUser.getRoleName());
        Role newRole;

        switch (oldRole) {
            case ROOM_MODERATOR:
                newRole = Role.ROOM_USER;
                break;
            case ROOM_ADMIN:
                newRole = Role.ROOM_MODERATOR;
                break;
            default:
                newRole = oldRole;
                break;
        }

        roomUser.setRoleName(newRole.getRoleName());
        update(roomUser);

        return authorityProvider.getPrivilegeListByRoleName(newRole);
    }

    @Override
    public boolean hasRoomPrivilege(String userUuid, Privilege privilege) throws DatabaseException {
        Optional<RoomUser> roomUserOpt = getRoomUser(userUuid);
        return roomUserOpt.isPresent() && authorityProvider.hasPrivilege(Role.getRole(roomUserOpt.get().getRoleName()), privilege);
    }

    @Override
    public boolean hasRoomRole(String userUuid, Role role) throws DatabaseException {
        Optional<RoomUser> roomUserOpt = getRoomUser(userUuid);
        return roomUserOpt.isPresent() && role.equals(Role.getRole(roomUserOpt.get().getRoleName()));
    }

    private RoomUser getSafeRoomUser(String roomUserUuid) throws DatabaseException, InvalidArgumentException {
        Optional<RoomUser> roomUserOpt = getById(roomUserUuid);

        if (roomUserOpt.isPresent()) {
            return roomUserOpt.get();
        } else {
            throw new NotFoundException("Room user not found");
        }
    }
}
