package vip.yazilim.p2g.web.service.p2g.impl;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.config.security.PasswordEncoderConfig;
import vip.yazilim.p2g.web.config.security.authority.AAuthorityProvider;
import vip.yazilim.p2g.web.constant.enums.Privilege;
import vip.yazilim.p2g.web.constant.enums.Role;
import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.entity.RoomInvite;
import vip.yazilim.p2g.web.entity.RoomUser;
import vip.yazilim.p2g.web.repository.IRoomUserRepo;
import vip.yazilim.p2g.web.service.p2g.IRoomInviteService;
import vip.yazilim.p2g.web.service.p2g.IRoomService;
import vip.yazilim.p2g.web.service.p2g.IRoomUserService;
import vip.yazilim.p2g.web.service.spotify.impl.SpotifyPlayerService;
import vip.yazilim.p2g.web.util.SecurityHelper;
import vip.yazilim.p2g.web.util.TimeHelper;
import vip.yazilim.spring.core.exception.general.InvalidArgumentException;
import vip.yazilim.spring.core.exception.general.InvalidUpdateException;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;
import vip.yazilim.spring.core.exception.general.database.DatabaseReadException;
import vip.yazilim.spring.core.exception.web.NotFoundException;
import vip.yazilim.spring.core.service.ACrudServiceImpl;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 2.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Transactional
@Service
public class RoomUserService extends ACrudServiceImpl<RoomUser, Long> implements IRoomUserService {

    @Autowired
    private IRoomUserRepo roomUserRepo;

    @Autowired
    private IRoomService roomService;

    @Autowired
    private IRoomInviteService roomInviteService;

    @Autowired
    private AAuthorityProvider authorityProvider;

    @Autowired
    private PasswordEncoderConfig passwordEncoderConfig;

    @Autowired
    private SpotifyPlayerService spotifyPlayerService;

    @Override
    protected JpaRepository<RoomUser, Long> getRepository() {
        return roomUserRepo;
    }

    @Override
    protected Long getId(RoomUser entity) {
        return entity.getId();
    }

    @Override
    protected RoomUser preInsert(RoomUser entity) {
        entity.setJoinDate(TimeHelper.getLocalDateTimeNow());
        return entity;
    }

    @Override
    public List<RoomUser> getRoomUsersByRoomId(Long roomId) throws DatabaseException {
        try {
            return roomUserRepo.findRoomUserByRoomIdOrderById(roomId);
        } catch (Exception exception) {
            String errMsg = String.format("An error occurred while getting RoomUser with Room[%s]", roomId);
            throw new DatabaseReadException(errMsg, exception);
        }
    }

    @Override
    public Optional<RoomUser> getRoomUser(String userId) throws DatabaseException {
        try {
            return roomUserRepo.findRoomUserByUserId(userId);
        } catch (Exception exception) {
            String errMsg = String.format("An error occurred while getting RoomUser with User[%s]", userId);
            throw new DatabaseReadException(errMsg, exception);
        }
    }

    @Override
    public Optional<RoomUser> getRoomUser(Long roomId, String userId) throws DatabaseException {
        try {
            return roomUserRepo.findRoomUserByRoomIdAndUserId(roomId, userId);
        } catch (Exception exception) {
            String errMsg = String.format("An error occurred while getting RoomUser with Room[%s], User[%s]", roomId, userId);
            throw new DatabaseReadException(errMsg, exception);
        }
    }

    @Override
    public RoomUser joinRoom(Long roomId, String password, Role role) throws DatabaseException, InvalidArgumentException, IOException, SpotifyWebApiException {
        String userId = SecurityHelper.getUserId();
        Optional<Room> roomOpt = roomService.getById(roomId);

        if (!roomOpt.isPresent()) {
            String err = String.format("Room[%s] can not found", roomId);
            throw new InvalidArgumentException(err);
        }

        Room room = roomOpt.get();
        RoomUser roomUser = new RoomUser();

        if (passwordEncoderConfig.passwordEncoder().matches(password, room.getPassword())) {
            roomUser.setRoomId(roomId);
            roomUser.setUserId(userId);
            roomUser.setRole(role.getRole());
            roomUser.setActiveFlag(true);
        } else {
            throw new InvalidArgumentException("Wrong password");
        }

        RoomUser joinedUser = create(roomUser);
        spotifyPlayerService.userSyncWithRoom(joinedUser);

        return roomUser;
    }

    //todo: for tests, delete later
    @Override
    public RoomUser joinRoom(Long roomId, String userId, String password, Role role) throws DatabaseException, InvalidArgumentException, IOException, SpotifyWebApiException {
        Optional<Room> roomOpt = roomService.getById(roomId);

        if (!roomOpt.isPresent()) {
            String err = String.format("Room[%s] can not found", roomId);
            throw new InvalidArgumentException(err);
        }

        Room room = roomOpt.get();
        RoomUser roomUser = new RoomUser();

        if (passwordEncoderConfig.passwordEncoder().matches(password, room.getPassword())) {
            roomUser.setRoomId(roomId);
            roomUser.setUserId(userId);
            roomUser.setRole(role.getRole());
            roomUser.setActiveFlag(true);
        } else {
            throw new InvalidArgumentException("Wrong password");
        }

        RoomUser joinedUser = create(roomUser);
        spotifyPlayerService.userSyncWithRoom(joinedUser);

        return roomUser;
    }

    @Override
    public RoomUser joinRoomOwner(Long roomId, String userId) throws DatabaseException, InvalidArgumentException {
        RoomUser roomUser = new RoomUser();

        roomUser.setRoomId(roomId);
        roomUser.setUserId(userId);
        roomUser.setRole(Role.ROOM_OWNER.getRole());
        roomUser.setActiveFlag(true);

        return create(roomUser);
    }

    @Override
    public boolean leaveRoom() throws DatabaseException, InvalidArgumentException {
        String userId = SecurityHelper.getUserId();
        Optional<RoomUser> roomUser = getRoomUser(userId);

        if (roomUser.isPresent()) {
            if (roomUser.get().getRole().equals(Role.ROOM_OWNER.getRole())) {
                return roomService.deleteById(roomUser.get().getRoomId());
            } else {
                return deleteById(roomUser.get().getId());
            }
        } else {
            throw new NotFoundException("User not in any room");
        }
    }

    @Override
    public RoomUser acceptRoomInvite(RoomInvite roomInvite) throws DatabaseException, InvalidArgumentException {
        if (roomInviteService.existsById(roomInvite.getId())) {
            RoomUser roomUser = new RoomUser();

            roomUser.setRoomId(roomInvite.getRoomId());
            roomUser.setUserId(roomInvite.getUserId());
            roomUser.setRole(Role.ROOM_USER.getRole());
            roomUser.setActiveFlag(true);

            return create(roomUser);
        } else {
            String err = String.format("Room Invite[%s] not found", roomInvite.getId());
            throw new NotFoundException(err);
        }
    }

    @Override
    public Role getRoleByRoomIdAndUserId(Long roomId, String userId) throws DatabaseException {
        Optional<RoomUser> roomUserOpt = getRoomUser(userId);

        if (!roomUserOpt.isPresent()) {
            return null;
        }

        String role = roomUserOpt.get().getRole();
        return Role.getRole(role);
    }

    @Override
    public boolean deleteRoomUsers(Long roomId) throws DatabaseException {
        List<RoomUser> roomUserList = roomUserRepo.findRoomUserByRoomIdOrderById(roomId);

        for (RoomUser roomUser : roomUserList) {
            delete(roomUser);
        }

        return true;
    }

    @Override
    public RoomUser promoteUserRole(Long roomUserId) throws DatabaseException, InvalidUpdateException, InvalidArgumentException {
        RoomUser roomUser = getSafeRoomUser(roomUserId);

        Role oldRole = Role.getRole(roomUser.getRole());
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

        roomUser.setRole(newRole.getRole());
        return update(roomUser);
    }

    @Override
    public RoomUser demoteUserRole(Long roomUserId) throws DatabaseException, InvalidUpdateException, InvalidArgumentException {
        RoomUser roomUser = getSafeRoomUser(roomUserId);

        Role oldRole = Role.getRole(roomUser.getRole());
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

        roomUser.setRole(newRole.getRole());
        return update(roomUser);
    }

    @Override
    public boolean hasRoomPrivilege(String userId, Privilege privilege) throws DatabaseException {
        Optional<RoomUser> roomUserOpt = getRoomUser(userId);
        return roomUserOpt.isPresent() && authorityProvider.hasPrivilege(Role.getRole(roomUserOpt.get().getRole()), privilege);
    }

    @Override
    public boolean hasRoomRole(String userId, Role role) throws DatabaseException {
        Optional<RoomUser> roomUserOpt = getRoomUser(userId);
        return roomUserOpt.isPresent() && role.equals(Role.getRole(roomUserOpt.get().getRole()));
    }

    private RoomUser getSafeRoomUser(Long roomUserId) throws DatabaseException, InvalidArgumentException {
        Optional<RoomUser> roomUserOpt = getById(roomUserId);

        if (roomUserOpt.isPresent()) {
            return roomUserOpt.get();
        } else {
            throw new NotFoundException("Room user not found");
        }
    }
}
