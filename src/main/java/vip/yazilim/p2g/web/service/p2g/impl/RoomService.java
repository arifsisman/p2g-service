package vip.yazilim.p2g.web.service.p2g.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.entity.relation.RoomUser;
import vip.yazilim.p2g.web.exception.RoomException;
import vip.yazilim.p2g.web.model.RoomModel;
import vip.yazilim.p2g.web.repository.IRoomRepo;
import vip.yazilim.p2g.web.service.p2g.IRoomService;
import vip.yazilim.p2g.web.service.p2g.IUserService;
import vip.yazilim.p2g.web.service.p2g.relation.IRoomInviteService;
import vip.yazilim.p2g.web.service.p2g.relation.IRoomUserService;
import vip.yazilim.p2g.web.util.DBHelper;
import vip.yazilim.p2g.web.util.TimeHelper;
import vip.yazilim.spring.core.exception.InvalidArgumentException;
import vip.yazilim.spring.core.exception.database.DatabaseException;
import vip.yazilim.spring.core.service.ACrudServiceImpl;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * * @author mustafaarifsisman - 31.10.2019
 *
 * @contact mustafaarifsisman@gmail.com
 */
@Transactional
@Service
public class RoomService extends ACrudServiceImpl<Room, String> implements IRoomService {

    @Autowired
    private IRoomRepo roomRepo;

    @Autowired
    private IRoomUserService roomUserService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IRoomInviteService roomInviteService;

    @Override
    protected JpaRepository<Room, String> getRepository() {
        return roomRepo;
    }

    @Override
    protected String getId(Room entity) {
        return entity.getUuid();
    }

    @Override
    protected Room preInsert(Room entity) {
        entity.setUuid(DBHelper.getRandomUuid());
        return entity;
    }

    @Override
    public Optional<Room> getRoomByUserUuid(String userUuid) throws DatabaseException, RoomException {
        Optional<Room> room;
        RoomUser roomUser;

        roomUser = roomUserService.getRoomUser(userUuid);

        try {
            room = getById(roomUser.getRoomUuid());
        } catch (Exception exception) {
            String err = String.format("An error occurred while getting Room with userUuid[%s]", userUuid);
            throw new RoomException(err, exception);
        }

        if (!room.isPresent()) {
            String err = String.format("Room[%s] is not present!", userUuid);
            throw new RoomException(err);
        }

        return room;
    }

    @Override
    public Optional<RoomModel> getRoomModelByRoomUuid(String roomUuid) throws DatabaseException, RoomException, InvalidArgumentException {
        RoomModel roomModel = new RoomModel();

        Optional<Room> room;

        List<User> userList;
        List<User> invitedUserList;

        String chatUuid;

        // Set Room
        room = getById(roomUuid);
        if (!room.isPresent()) {
            String err = String.format("Room[%s] is not present", roomUuid);
            throw new RoomException(err);
        } else {
            roomModel.setRoom(room.get());
        }

        // Set User List
        userList = userService.getUsersByRoomUuid(roomUuid);
        roomModel.setUserList(userList);

        // Set Invited User List
        invitedUserList = roomInviteService.getInvitedUserListByRoomUuid(roomUuid);
        roomModel.setInvitedUserList(invitedUserList);

        // Set Room Chat Uuid
        chatUuid = room.get().getChatUuid();
        roomModel.setChatUuid(chatUuid);

        return Optional.of(roomModel);
    }

    @Override
    public Room createRoom(String name, String ownerUuid, String password, Integer maxUsers,
                           Boolean usersAllowedQueue, Boolean usersAllowedControl, String chatUuid) throws RoomException {
        Room room = new Room();

        room.setName(name);
        room.setOwnerUuid(ownerUuid);
        room.setCreationDate(TimeHelper.getCurrentTime());

        if (password == null) {
            room.setPrivateFlag(false);
            room.setShowRoomActivityFlag(true);
        } else {
            room.setPassword(password);
            room.setPrivateFlag(true);
            room.setShowRoomActivityFlag(false);
        }

        room.setMaxUsers(maxUsers == null ? Integer.valueOf(5) : maxUsers);
        room.setUsersAllowedQueueFlag(usersAllowedQueue == null ? usersAllowedQueue : Boolean.valueOf(false));
        room.setUsersAllowedControlFlag(usersAllowedControl == null ? usersAllowedControl : Boolean.valueOf(false));
        room.setActiveFlag(true);
        room.setChatUuid(chatUuid);

        try {
            create(room);
        } catch (DatabaseException e) {
            throw new RoomException("Room cannot created!", e);
        }

        return room;
    }

    @Override
    public boolean deleteRoom(String roomUuid) throws DatabaseException, InvalidArgumentException, RoomException {
        Optional<Room> roomOpt = getById(roomUuid);

        if (roomOpt.isPresent()) {
            return delete(roomOpt.get());
        } else {
            throw new RoomException("Room cannot deleted!");
        }
    }
}
