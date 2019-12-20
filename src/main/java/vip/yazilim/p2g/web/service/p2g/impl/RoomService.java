package vip.yazilim.p2g.web.service.p2g.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.config.security.SecurityConfig;
import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.entity.relation.RoomUser;
import vip.yazilim.p2g.web.model.RoomModel;
import vip.yazilim.p2g.web.repository.IRoomRepo;
import vip.yazilim.p2g.web.service.p2g.IRoomService;
import vip.yazilim.p2g.web.service.p2g.IUserService;
import vip.yazilim.p2g.web.service.p2g.relation.IRoomInviteService;
import vip.yazilim.p2g.web.service.p2g.relation.IRoomUserService;
import vip.yazilim.p2g.web.service.p2g.relation.ISongService;
import vip.yazilim.p2g.web.util.DBHelper;
import vip.yazilim.p2g.web.util.TimeHelper;
import vip.yazilim.spring.core.exception.general.InvalidArgumentException;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;
import vip.yazilim.spring.core.exception.general.database.DatabaseReadException;
import vip.yazilim.spring.core.exception.web.NotFoundException;
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

    @Autowired
    private ISongService songService;

    @Autowired
    private SecurityConfig securityConfig;

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
        //TODO: firebase chat uuid
        entity.setUuid(DBHelper.getRandomUuid());
        entity.setPassword(securityConfig.passwordEncoder().encode(entity.getPassword()));
        entity.setCreationDate(TimeHelper.getCurrentTimestamp());
        return entity;
    }

    @Override
    public Optional<Room> getRoomByUserUuid(String userUuid) throws DatabaseException {
        Optional<Room> room;
        RoomUser roomUser;

        Optional<RoomUser> roomUserOpt = roomUserService.getRoomUser(userUuid);

        if (roomUserOpt.isPresent()) {
            roomUser = roomUserOpt.get();
        } else {
            String err = String.format("user[%s] not in any room", userUuid);
            throw new NotFoundException(err);
        }

        try {
            room = getById(roomUser.getRoomUuid());
        } catch (Exception exception) {
            String err = String.format("An error occurred while getting Room with userUuid[%s]", userUuid);
            throw new DatabaseReadException(err, exception);
        }

        if (!room.isPresent()) {
            String err = String.format("Room[%s] not found", userUuid);
            throw new NotFoundException(err);
        }

        return room;
    }

    @Override
    public RoomModel getRoomModelByRoomUuid(String roomUuid) throws DatabaseException, InvalidArgumentException {
        RoomModel roomModel = new RoomModel();

        Optional<Room> room;

        List<User> userList;
        List<User> invitedUserList;

        String chatUuid;

        // Set Room
        room = getById(roomUuid);
        if (!room.isPresent()) {
            String err = String.format("Room[%s] not found", roomUuid);
            throw new NotFoundException(err);
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

        return roomModel;
    }

    //TODO: for tests, delete later
    @Override
    public Room createRoom(String ownerUuid, String roomName, String roomPassword) throws DatabaseException, InvalidArgumentException {
        Room room = new Room();

        room.setOwnerUuid(ownerUuid);
        room.setName(roomName);
        room.setPassword(roomPassword);

        room.setPrivateFlag(false);

        return create(room);
    }


    @Override
    public Room create(Room room) throws DatabaseException, InvalidArgumentException {
        Room createdRoom = super.create(room);
        roomUserService.joinRoomOwner(createdRoom.getUuid(), createdRoom.getOwnerUuid());
        return room;
    }

    @Override
    public boolean deleteById(String roomUuid) throws DatabaseException, InvalidArgumentException {
        Optional<Room> roomOpt = getById(roomUuid);

        boolean status = false;

        if (roomOpt.isPresent()) {
            status = delete(roomOpt.get());

            //delete roomUsers
            roomUserService.deleteRoomUsers(roomUuid);

            //delete Songs
            songService.deleteRoomSongList(roomUuid);

            //delete roomInvites
            roomInviteService.deleteRoomInvites(roomUuid);
        }

        return status;
    }
}
