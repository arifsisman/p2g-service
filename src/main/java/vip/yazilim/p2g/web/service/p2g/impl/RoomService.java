package vip.yazilim.p2g.web.service.p2g.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.config.security.PasswordEncoderConfig;
import vip.yazilim.p2g.web.constant.enums.RoomStatus;
import vip.yazilim.p2g.web.controller.websocket.WebSocketController;
import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.entity.RoomUser;
import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.model.RoomModel;
import vip.yazilim.p2g.web.repository.IRoomRepo;
import vip.yazilim.p2g.web.service.p2g.*;
import vip.yazilim.p2g.web.util.TimeHelper;
import vip.yazilim.spring.core.exception.general.InvalidArgumentException;
import vip.yazilim.spring.core.exception.general.InvalidUpdateException;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;
import vip.yazilim.spring.core.exception.general.database.DatabaseReadException;
import vip.yazilim.spring.core.exception.web.NotFoundException;
import vip.yazilim.spring.core.service.ACrudServiceImpl;

import javax.transaction.Transactional;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * * @author mustafaarifsisman - 31.10.2019
 *
 * @contact mustafaarifsisman@gmail.com
 */
@Transactional
@Service
public class RoomService extends ACrudServiceImpl<Room, Long> implements IRoomService {

    @Autowired
    private IRoomRepo roomRepo;

    @Autowired
    private IRoomUserService roomUserService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IRoomInviteService roomInviteService;

    @Autowired
    private ISongService songService;

    @Autowired
    private PasswordEncoderConfig passwordEncoderConfig;

    @Autowired
    private WebSocketController webSocketController;

    @Override
    protected JpaRepository<Room, Long> getRepository() {
        return roomRepo;
    }

    @Override
    protected Long getId(Room entity) {
        return entity.getId();
    }

    @Override
    protected Room preInsert(Room entity) {
        if (entity.getPassword() == null || entity.getPassword().equals("\"\"")) {
            entity.setPassword("");
            entity.setPrivateFlag(false);
        } else {
            entity.setPassword(passwordEncoderConfig.passwordEncoder().encode(entity.getPassword()));
            entity.setPrivateFlag(true);
        }
        entity.setCreationDate(TimeHelper.getLocalDateTimeNow());
        entity.setActiveFlag(true);
        entity.setUsersAllowedQueueFlag(false);
        entity.setUsersAllowedControlFlag(false);
        entity.setShowRoomActivityFlag(true);
        entity.setMaxUsers(50);

        return entity;
    }

    @Override
    public Optional<Room> getRoomByUserId(String userId) throws DatabaseException {
        Optional<Room> room;
        RoomUser roomUser;

        Optional<RoomUser> roomUserOpt = roomUserService.getRoomUser(userId);

        if (roomUserOpt.isPresent()) {
            roomUser = roomUserOpt.get();
        } else {
            return Optional.empty();
        }

        try {
            room = getById(roomUser.getRoomId());
        } catch (Exception exception) {
            String err = String.format("An error occurred while getting Room with userId[%s]", userId);
            throw new DatabaseReadException(err, exception);
        }

        if (!room.isPresent()) {
            String err = String.format("Room[%s] not found", userId);
            throw new NotFoundException(err);
        }

        return room;
    }

    @Override
    public List<RoomModel> getRoomModels() throws DatabaseException, InvalidArgumentException {
        List<RoomModel> roomModelList = new LinkedList<>();
        List<Room> roomList = getAll();

        for (Room r : roomList) {
            roomModelList.add(getRoomModelByRoomId(r.getId()));
        }

        return roomModelList;
    }

    @Override
    public RoomModel getRoomModelByRoomId(Long roomId) throws DatabaseException, InvalidArgumentException {
        RoomModel roomModel = new RoomModel();

        Optional<Room> room;
        List<User> userList;
        List<Song> songList;
        List<User> invitedUserList;

        // Set Room
        room = getById(roomId);
        if (!room.isPresent()) {
            String err = String.format("Room[%s] not found", roomId);
            throw new NotFoundException(err);
        } else {
            roomModel.setRoom(room.get());
        }

        // Set User List
        userList = userService.getUsersByRoomId(roomId);
        roomModel.setUserList(userList);

        // Set owner
        Optional<RoomUser> roomOwnerOpt = roomUserService.getRoomOwner(roomId);
        if (roomOwnerOpt.isPresent()) {
            Optional<User> roomUser = userService.getById(roomOwnerOpt.get().getUserId());
            roomModel.setOwner(roomUser.orElseThrow(() -> new NotFoundException("Room owner not found")));
        }

        // Set Room Users
        roomModel.setRoomUserList(roomUserService.getRoomUsersByRoomId(roomId));

        // Set Song List
        songList = songService.getSongListByRoomId(roomId);
        roomModel.setSongList(songList);

        // Set Invited User List
        invitedUserList = roomInviteService.getInvitedUserListByRoomId(roomId);
        roomModel.setInvitedUserList(invitedUserList);

        return roomModel;
    }

    @Override
    public Room createRoom(String ownerId, String roomName, String roomPassword) throws DatabaseException, InvalidArgumentException {
        Room room = new Room();

        room.setOwnerId(ownerId);
        room.setName(roomName);
        room.setPassword(roomPassword);
        userService.getById(ownerId).ifPresent(u -> room.setCountryCode(u.getCountryCode()));

        return create(room);
    }

    @Override
    public Room create(Room room) throws DatabaseException, InvalidArgumentException {
        Room createdRoom = super.create(room);
        // Post create
        roomUserService.joinRoomOwner(createdRoom.getId(), createdRoom.getOwnerId());
        return room;
    }

    @Override
    public boolean deleteById(Long roomId) throws DatabaseException, InvalidArgumentException {
        Optional<Room> roomOpt = getById(roomId);

        boolean status = true;

        if (roomOpt.isPresent()) {
            status = delete(roomOpt.get());

            //delete roomUsers
            status &= roomUserService.deleteRoomUsers(roomId);

            //delete Songs
            status &= songService.deleteRoomSongList(roomId);

            //delete roomInvites
            status &= roomInviteService.deleteRoomInvites(roomId);
        }

        webSocketController.sendToRoom("status", roomId, RoomStatus.CLOSED);
        return status;
    }

    @Override
    public Room update(Room room) throws InvalidUpdateException, DatabaseException, InvalidArgumentException {
        room = super.update(room);
        webSocketController.sendToRoom("status", room.getId(), RoomStatus.UPDATED);
        return room;
    }
}
