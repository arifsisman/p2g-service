package vip.yazilim.p2g.web.service.p2g.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.libs.springcore.exception.DatabaseReadException;
import vip.yazilim.libs.springcore.service.ACrudServiceImpl;
import vip.yazilim.p2g.web.config.security.PasswordEncoderConfig;
import vip.yazilim.p2g.web.controller.WebSocketController;
import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.entity.RoomUser;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.enums.RoomStatus;
import vip.yazilim.p2g.web.model.RoomModel;
import vip.yazilim.p2g.web.model.RoomStatusModel;
import vip.yazilim.p2g.web.model.RoomUserModel;
import vip.yazilim.p2g.web.repository.IRoomRepo;
import vip.yazilim.p2g.web.service.p2g.*;
import vip.yazilim.p2g.web.service.spotify.IPlayerService;
import vip.yazilim.p2g.web.util.SecurityHelper;
import vip.yazilim.p2g.web.util.TimeHelper;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private IPlayerService spotifyPlayerService;

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
        if (entity.getPassword() == null || entity.getPassword().isEmpty()) {
            entity.setPassword(null);
            entity.setPrivateFlag(false);
        } else {
            entity.setPassword(passwordEncoderConfig.passwordEncoder().encode(entity.getPassword()));
            entity.setPrivateFlag(true);
        }
        entity.setCreationDate(TimeHelper.getLocalDateTimeNow());
        entity.setActiveFlag(false);
        entity.setMaxUsers(50);

        return entity;
    }

    @Override
    public Optional<Room> getRoomByUserId(String userId) {
        Optional<Room> room;
        RoomUser roomUser;

        Optional<RoomUser> roomUserOpt = roomUserService.getRoomUserByUserId(userId);

        if (roomUserOpt.isPresent()) {
            roomUser = roomUserOpt.get();
        } else {
            return Optional.empty();
        }

        room = getById(roomUser.getRoomId());

        if (!room.isPresent()) {
            throw new NoSuchElementException("Room :: Not found");
        }

        return room;
    }

    @Override
    public List<RoomModel> getRoomModels() {
        return getAll().stream().map(this::getRoomModelWithRoom).collect(Collectors.toList());
    }

    @Override
    public Optional<RoomModel> getRoomModelByRoomId(Long roomId) {
        // Set Room
        Optional<Room> roomOpt = getById(roomId);
        if (!roomOpt.isPresent()) {
            String err = String.format("Room[%s] :: Not found", roomId);
            throw new NoSuchElementException(err);
        } else {
            Room room = roomOpt.get();

            RoomModel roomModel = new RoomModel();
            roomModel.setRoom(room);

            return getRoomModelSimplifiedBase(roomModel);
        }
    }

    @Override
    public RoomModel getRoomModelWithRoom(Room room) {
        RoomModel roomModel = new RoomModel();
        roomModel.setRoom(room);
        return getRoomModelSimplifiedBase(roomModel).orElseThrow(() -> new NoSuchElementException("Room[" + room.getId() + "] :: Error when creating RoomModel."));
    }

    private Optional<RoomModel> getRoomModelSimplifiedBase(RoomModel roomModel) {
        Room room = roomModel.getRoom();
        Long roomId = room.getId();

        // Set owner
        Optional<User> ownerOpt = userService.getById(room.getOwnerId());

        if (ownerOpt.isPresent()) {
            roomModel.setOwner(ownerOpt.get());
            songService.getRecentSong(room.getId()).ifPresent(roomModel::setSong);
            roomModel.setUserCount(roomUserService.getRoomUserCountByRoomId(roomId));

            return Optional.of(roomModel);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public RoomUserModel createRoom(String ownerId, String roomName, String roomPassword) {
        // Any room exists check
        Optional<RoomUser> existingUserOpt = roomUserService.getRoomUserByUserId(ownerId);
        if (existingUserOpt.isPresent()) {
            roomUserService.leaveRoom();
        }

        RoomUserModel roomUserModel = new RoomUserModel();
        Optional<User> ownerOpt = userService.getById(ownerId);
        if (ownerOpt.isPresent()) {
            Room room = new Room();
            room.setOwnerId(ownerId);
            room.setName(roomName);
            room.setPassword(roomPassword);

            Room createdRoom = create(room);
            RoomUser joinedRoomOwner = roomUserService.joinRoomOwner(createdRoom.getId(), createdRoom.getOwnerId());

            LOGGER.info("[{}] :: Created Room[{}]", ownerId, createdRoom.getId());

            roomUserModel.setRoom(createdRoom);
            roomUserModel.setRoomUser(joinedRoomOwner);

            return roomUserModel;
        } else {
            throw new NoSuchElementException("User :: Not found");
        }
    }

    @Override
    public Optional<RoomModel> getRoomModelByUserId(String userId) {
        Optional<RoomUser> roomUserOpt = roomUserService.getRoomUserByUserId(userId);

        if (roomUserOpt.isPresent()) {
            return getRoomModelByRoomId(roomUserOpt.get().getRoomId());
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<Room> getActiveRooms() {
        try {
            return roomRepo.findByActiveFlag(true);
        } catch (Exception exception) {
            throw new DatabaseReadException(getClassOfEntity(), exception);
        }
    }

    @Override
    public Optional<User> getRoomOwner(Long roomId) {
        Optional<Room> roomOpt = getById(roomId);

        if (roomOpt.isPresent()) {
            return userService.getById(roomOpt.get().getOwnerId());
        } else {
            throw new NoSuchElementException("Room not found");
        }
    }

    @Override
    public boolean deleteById(Long roomId) {
        try {
            spotifyPlayerService.roomStop(roomId);
        } catch (Exception e) {
            LOGGER.warn("Room[{}] :: An error occurred when stopping playback", roomId);
        }

        // Delete roomUsers
        roomUserService.deleteRoomUsers(roomId);

        // Delete Songs
        songService.deleteRoomSongList(roomId);

        // Delete roomInvites
        roomInviteService.deleteRoomInvites(roomId);

        try {
            webSocketController.sendToRoom("status", roomId, new RoomStatusModel(RoomStatus.CLOSED, "Room closed by :: " + SecurityHelper.getUserDisplayName()));
        } catch (Exception ignored) {
        }

        return super.deleteById(roomId);
    }

    @Override
    public boolean delete(Room room) {
        return deleteById(room.getId());
    }

    @Override
    public Room update(Room room) {
        room = super.update(room);
        webSocketController.sendToRoom("status", room.getId(), RoomStatus.UPDATED);
        return room;
    }
}
