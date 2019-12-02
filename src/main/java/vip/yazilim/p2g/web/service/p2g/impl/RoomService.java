package vip.yazilim.p2g.web.service.p2g.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.constant.Roles;
import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.entity.relation.RoomQueue;
import vip.yazilim.p2g.web.entity.relation.RoomUser;
import vip.yazilim.p2g.web.exception.RoomException;
import vip.yazilim.p2g.web.model.RoomModel;
import vip.yazilim.p2g.web.repository.IRoomRepo;
import vip.yazilim.p2g.web.service.p2g.IRoomService;
import vip.yazilim.p2g.web.service.p2g.IUserService;
import vip.yazilim.p2g.web.service.p2g.relation.IRoomInviteService;
import vip.yazilim.p2g.web.service.p2g.relation.IRoomQueueService;
import vip.yazilim.p2g.web.service.p2g.relation.IRoomUserService;
import vip.yazilim.p2g.web.util.TimeHelper;
import vip.yazilim.spring.utils.exception.DatabaseException;
import vip.yazilim.spring.utils.service.ACrudServiceImpl;

import java.util.List;
import java.util.Optional;

/**
 * * @author mustafaarifsisman - 31.10.2019
 *
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class RoomService extends ACrudServiceImpl<Room, String> implements IRoomService {

    // static fields
    private Logger LOGGER = LoggerFactory.getLogger(RoomService.class);

    // injected dependencies
    @Autowired
    private IRoomRepo roomRepo;

    @Autowired
    private IRoomUserService roomUserService;

    @Autowired
    private IRoomQueueService roomQueueService;

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

    //TODO: uncomment
//    @Override
//    protected Room preInsert(Room entity) {
//        entity.setUuid(DBHelper.getRandomUuid());
//        return entity;
//    }

    @Override
    public Optional<Room> getRoomByUserUuid(String userUuid) throws DatabaseException {
        Optional<Room> room;
        Optional<RoomUser> roomUser;
        String roomUuid;

        roomUser = roomUserService.getRoomUserByUserUuid(userUuid);

        if (roomUser.isPresent()) {
            roomUuid = roomUser.get().getRoomUuid();
        } else {
            LOGGER.warn("User[{}] not in any Room!", userUuid);
            return Optional.empty();
        }

        try {
            room = getById(roomUuid);
        } catch (Exception exception) {
            String errorMessage = String.format("An error occurred while getting Room with userUuid[%s]", userUuid);
            throw new DatabaseException(errorMessage, exception);
        }

        if (!room.isPresent()) {
            LOGGER.warn("Room[{}] is not present!", roomUuid);
            return Optional.empty();
        }

        return room;
    }

    @Override
    public Optional<RoomModel> getRoomModelByRoomUuid(String roomUuid) throws DatabaseException {
        RoomModel roomModel = new RoomModel();

        Optional<Room> room;

        List<User> userList;
        List<User> invitedUserList;

        List<RoomQueue> roomQueueList;
        RoomQueue nowPlaying;

        String chatUuid;

        // Set Room
        room = getById(roomUuid);
        if (!room.isPresent()) {
            LOGGER.error("Room[{}] is not present", roomUuid);
            return Optional.empty();
        } else {
            roomModel.setRoom(room.get());
        }

        // Set User List
        userList = userService.getUsersByRoomUuid(roomUuid);
        roomModel.setUserList(userList);

        // Set Invited User List
        invitedUserList = roomInviteService.getInvitedUserListByRoomUuid(roomUuid);
        roomModel.setInvitedUserList(invitedUserList);

        // Set RoomQueue
        roomQueueList = roomQueueService.getQueueListByRoomUuid(roomUuid);
        roomModel.setRoomQueueList(roomQueueList);

        // Set Now Playing
        nowPlaying = roomQueueService.getRoomQueueNowPlaying(roomUuid);
        roomModel.setNowPlaying(nowPlaying);

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

        if (maxUsers == null) {
            room.setMaxUsers(5);
        } else {
            room.setMaxUsers(maxUsers);
        }

        if (usersAllowedQueue == null) {
            room.setUsersAllowedQueueFlag(usersAllowedQueue);
        } else {
            room.setUsersAllowedQueueFlag(false);
        }

        if (usersAllowedControl == null) {
            room.setUsersAllowedControlFlag(usersAllowedControl);
        } else {
            room.setUsersAllowedControlFlag(false);
        }

        room.setActiveFlag(true);
        room.setChatUuid(chatUuid);

        try {
            create(room);
        } catch (DatabaseException e) {
            LOGGER.error("Room cannot created!");
            throw new RoomException("Room cannot created!", e);
        }
        return room;
    }

    @Override
    public RoomUser joinRoom(String roomUuid, String userUuid) {
        RoomUser roomUser = new RoomUser();

        roomUser.setRoomUuid(roomUuid);
        roomUser.setUserUuid(userUuid);
        roomUser.setRoleName(Roles.USER.getRoleName());
        roomUser.setActiveFlag(true);

        try {
            roomUserService.create(roomUser);
        } catch (DatabaseException e) {
            LOGGER.error("An error occurred when joining roomUuid[{}], userUuid[{}]", roomUuid, userUuid);
        }

        return roomUser;
    }
}
