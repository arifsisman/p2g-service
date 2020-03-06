package vip.yazilim.p2g.web.service.p2g.impl;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import vip.yazilim.p2g.web.model.RoomModelSimplified;
import vip.yazilim.p2g.web.repository.IRoomRepo;
import vip.yazilim.p2g.web.service.p2g.*;
import vip.yazilim.p2g.web.service.spotify.ISpotifyPlayerService;
import vip.yazilim.p2g.web.util.TimeHelper;
import vip.yazilim.spring.core.exception.GeneralException;
import vip.yazilim.spring.core.exception.InvalidArgumentException;
import vip.yazilim.spring.core.exception.database.DatabaseException;
import vip.yazilim.spring.core.exception.database.DatabaseReadException;
import vip.yazilim.spring.core.exception.web.NotFoundException;
import vip.yazilim.spring.core.service.ACrudServiceImpl;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * * @author mustafaarifsisman - 31.10.2019
 *
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class RoomService extends ACrudServiceImpl<Room, Long> implements IRoomService {

    private Logger LOGGER = LoggerFactory.getLogger(RoomService.class);

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

    @Autowired
    private ISpotifyPlayerService spotifyPlayerService;

    @Override
    protected JpaRepository<Room, Long> getRepository() {
        return roomRepo;
    }

    @Override
    protected Long getId(Room entity) {
        return entity.getId();
    }

    @Override
    protected Class<Room> getClassOfEntity() {
        return Room.class;
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
            throw new DatabaseReadException(getClassOfEntity(), exception, userId);
        }

        if (!room.isPresent()) {
            String err = String.format("Room[%s] not found", userId);
            throw new NotFoundException(err);
        }

        return room;
    }

    @Override
    public List<RoomModelSimplified> getSimplifiedRoomModels() throws DatabaseException, InvalidArgumentException {
        List<RoomModelSimplified> roomModelList = new LinkedList<>();
        List<Room> roomList = getAll();

        for (Room r : roomList) {
            roomModelList.add(getRoomModelSimplifiedWithRoom(r));
        }

        return roomModelList;
    }

    @Override
    public RoomModel getRoomModelByRoomId(Long roomId) throws DatabaseException, InvalidArgumentException {
        Optional<Room> room;

        // Set Room
        room = getById(roomId);
        if (!room.isPresent()) {
            String err = String.format("Room[%s] not found", roomId);
            throw new NotFoundException(err);
        } else {
            RoomModel roomModel = new RoomModel();
            roomModel.setRoom(room.get());

            return getRoomModelBase(roomModel);
        }
    }

    @Override
    public RoomModel getRoomModelWithRoom(Room room) throws DatabaseException, InvalidArgumentException {
        // Set Room
        RoomModel roomModel = new RoomModel();
        roomModel.setRoom(room);

        return getRoomModelBase(roomModel);
    }

    private RoomModel getRoomModelBase(RoomModel roomModel) throws DatabaseException, InvalidArgumentException {
        Room room = roomModel.getRoom();
        Long roomId = room.getId();

        // Set User List
        List<User> userList = userService.getUsersByRoomId(roomId);
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
        List<Song> songList = songService.getSongListByRoomId(roomId);
        roomModel.setSongList(songList);

        // Set Invited User List
        List<User> invitedUserList = roomInviteService.getInvitedUserListByRoomId(roomId);
        roomModel.setInvitedUserList(invitedUserList);

        // Set user count
        Integer userCount = roomUserService.getRoomUserCountByRoomId(roomId);
        roomModel.setUserCount(userCount);

        return roomModel;
    }

    @Override
    public RoomModel getRoomModelByUserId(String userId) throws DatabaseException, InvalidArgumentException {
        Optional<RoomUser> roomUser = roomUserService.getRoomUser(userId);
        if (roomUser.isPresent()) {
            return getRoomModelByRoomId(roomUser.get().getRoomId());
        } else {
            String msg = String.format("User[%s] not in room, acted normally.", userId);
            throw new NotFoundException(msg);
        }
    }

    @Override
    public RoomModelSimplified getRoomModelSimplifiedByRoomId(Long roomId) throws DatabaseException, InvalidArgumentException {
        // Set Room
        Optional<Room> room = getById(roomId);
        if (!room.isPresent()) {
            String err = String.format("Room[%s] not found", roomId);
            throw new NotFoundException(err);
        } else {
            RoomModelSimplified roomModelSimplified = new RoomModelSimplified();
            roomModelSimplified.setRoom(room.get());
            return getRoomModelSimplifiedBase(roomModelSimplified);
        }
    }

    @Override
    public RoomModelSimplified getRoomModelSimplifiedWithRoom(Room room) throws DatabaseException, InvalidArgumentException {
        RoomModelSimplified roomModelSimplified = new RoomModelSimplified();
        roomModelSimplified.setRoom(room);
        return getRoomModelSimplifiedBase(roomModelSimplified);
    }

    private RoomModelSimplified getRoomModelSimplifiedBase(RoomModelSimplified roomModelSimplified) throws DatabaseException, InvalidArgumentException {
        Long roomId = roomModelSimplified.getRoom().getId();

        // Set owner
        Optional<RoomUser> roomOwnerOpt = roomUserService.getRoomOwner(roomId);
        if (roomOwnerOpt.isPresent()) {
            Optional<User> roomUser = userService.getById(roomOwnerOpt.get().getUserId());
            roomModelSimplified.setOwner(roomUser.orElseThrow(() -> new NotFoundException("Room owner not found")));
        }

        // Set song list
        List<Song> songList = songService.getSongListByRoomId(roomId);
        if (!songList.isEmpty()) {
            roomModelSimplified.setSong(songList.get(0));
        }

        // Set user count
        Integer userCount = roomUserService.getRoomUserCountByRoomId(roomId);
        roomModelSimplified.setUserCount(userCount);

        return roomModelSimplified;
    }

    @Override
    public Room createRoom(String ownerId, String roomName, String roomPassword) throws GeneralException {
        // Any room exists check
        Optional<RoomUser> existingUserOpt = roomUserService.getRoomUser(ownerId);
        if (existingUserOpt.isPresent()) {
            roomUserService.leaveRoom();
        }

        Room room = new Room();
        room.setOwnerId(ownerId);
        room.setName(roomName);
        room.setPassword(roomPassword);

        Room createdRoom = create(room);
        roomUserService.joinRoomOwner(createdRoom.getId(), createdRoom.getOwnerId());

        LOGGER.info("User[{}] created Room[{}]", ownerId, createdRoom.getId());
        return createdRoom;
    }

    @Override
    public boolean deleteById(Long roomId) throws DatabaseException {
        try {
            spotifyPlayerService.roomStop(roomId);
        } catch (InvalidArgumentException | IOException | SpotifyWebApiException e) {
            LOGGER.warn("An error occurred when stopping Room[{}]", roomId);
        }

        //delete roomUsers
        roomUserService.deleteRoomUsers(roomId);

        //delete Songs
        songService.deleteRoomSongList(roomId);

        //delete roomInvites
        roomInviteService.deleteRoomInvites(roomId);

        webSocketController.sendToRoom("status", roomId, RoomStatus.CLOSED);

        return super.deleteById(roomId);
    }

    @Override
    public boolean delete(Room room) throws DatabaseException {
        return deleteById(room.getId());
    }

    @Override
    public Room update(Room room) throws DatabaseException, InvalidArgumentException {
        room = super.update(room);
        webSocketController.sendToRoom("status", room.getId(), RoomStatus.UPDATED);
        return room;
    }
}
