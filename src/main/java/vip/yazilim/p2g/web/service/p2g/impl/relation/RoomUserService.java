package vip.yazilim.p2g.web.service.p2g.impl.relation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.constant.Roles;
import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.entity.relation.RoomInvite;
import vip.yazilim.p2g.web.entity.relation.RoomUser;
import vip.yazilim.p2g.web.exception.InviteException;
import vip.yazilim.p2g.web.exception.RoomException;
import vip.yazilim.p2g.web.repository.relation.IRoomUserRepo;
import vip.yazilim.p2g.web.service.p2g.IRoomService;
import vip.yazilim.p2g.web.service.p2g.relation.IRoomInviteService;
import vip.yazilim.p2g.web.service.p2g.relation.IRoomUserService;
import vip.yazilim.p2g.web.util.DBHelper;
import vip.yazilim.spring.core.exception.general.InvalidArgumentException;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;
import vip.yazilim.spring.core.exception.general.database.DatabaseReadException;
import vip.yazilim.spring.core.service.ACrudServiceImpl;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
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
    public RoomUser joinRoom(String roomUuid, String userUuid, String password, Roles role) throws RoomException, DatabaseException, InvalidArgumentException {
        Optional<Room> roomOpt = roomService.getById(roomUuid);

        if (!roomOpt.isPresent()) {
            String err = String.format("Room[%s] can not found", roomUuid);
            throw new RoomException(err);
        }

        Room room = roomOpt.get();
        RoomUser roomUser = new RoomUser();

        if (Objects.equals(password, room.getPassword())) {
            roomUser.setRoomUuid(roomUuid);
            roomUser.setUserUuid(userUuid);
            roomUser.setRoleName(role.getRoleName());
            roomUser.setActiveFlag(true);
        } else {
            throw new RoomException("Wrong password.");
        }

        try {
            create(roomUser);
        } catch (DatabaseException e) {
            String err = String.format("An error occurred when joining roomUuid[%s], userUuid[%s]", roomUuid, userUuid);
            throw new RoomException(err, e);
        }

        return roomUser;
    }

    @Override
    public RoomUser acceptRoomInvite(RoomInvite roomInvite) throws DatabaseException, InviteException {
        if (roomInviteService.existsById(roomInvite.getUuid())) {
            RoomUser roomUser = new RoomUser();

            roomUser.setRoomUuid(roomInvite.getRoomUuid());
            roomUser.setUserUuid(roomInvite.getUserUuid());
            roomUser.setRoleName(Roles.ROOM_USER.getRoleName());
            roomUser.setActiveFlag(true);

            return create(roomUser);
        } else {
            String err = String.format("Room Invite[%s] cannot found.", roomInvite.getUuid());
            throw new InviteException(err);
        }
    }

    @Override
    public boolean deleteRoomUsers(String roomUuid) throws DatabaseException {
        List<RoomUser> roomUserList = roomUserRepo.findRoomUserByRoomUuidOrderByUuid(roomUuid);

        for (RoomUser roomUser : roomUserList) {
            delete(roomUser);
        }

        return true;
    }

}
