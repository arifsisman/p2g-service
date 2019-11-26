package vip.yazilim.p2g.web.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.relation.RoomQueue;
import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.entity.relation.RoomUser;
import vip.yazilim.p2g.web.model.RoomModel;
import vip.yazilim.p2g.web.repository.IRoomRepo;
import vip.yazilim.p2g.web.service.IQueueService;
import vip.yazilim.p2g.web.service.IRoomService;
import vip.yazilim.p2g.web.service.ISongService;
import vip.yazilim.p2g.web.service.IUserService;
import vip.yazilim.p2g.web.service.relation.IRoomInviteService;
import vip.yazilim.p2g.web.service.relation.IRoomUserService;
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
public class RoomServiceImpl extends ACrudServiceImpl<Room, String> implements IRoomService {

    // static fields
    private Logger LOGGER = LoggerFactory.getLogger(RoomServiceImpl.class);

    // injected dependencies
    @Autowired
    private IRoomRepo roomRepo;

    @Autowired
    private IRoomUserService roomUserService;

    @Autowired
    private IQueueService queueService;

    @Autowired
    private ISongService songService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IRoomInviteService roomInviteService;

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
            LOGGER.warn("User[{}] not in any Room!", userUuid);
            return Optional.empty();
        }

        return room;
    }

    @Override
    public Optional<RoomModel> getRoomModelByRoomUuid(String roomUuid) throws DatabaseException {
        RoomModel roomModel = new RoomModel();

        Optional<Room> room;
        List<RoomQueue> roomQueue;
        Optional<Song> nowPlaying;
        List<User> userList;
        String chatUuid;
        List<User> invitedUserList;

        room = getById(roomUuid);
        // Set Room
        if (!room.isPresent()) {
            LOGGER.error("Room cannot found with roomUuid: " + roomUuid);
            return Optional.empty();
        } else {
            roomModel.setRoom(room.get());
        }

        // Set RoomQueue
        roomQueue = queueService.getQueueListByRoomUuid(roomUuid);

        // Set Now Playing Song
        if (!roomQueue.isEmpty()) {
            RoomQueue roomQueue0 = roomQueue.get(0);
            String queue0SongUuid = roomQueue0.getSongUuid();
            Song nowPlayingSong = songService.getSafeSong(queue0SongUuid);
            nowPlaying = Optional.of(nowPlayingSong);
        } else {
            nowPlaying = Optional.empty();
        }
        roomModel.setNowPlaying(nowPlaying);

        // Set User List
        userList = userService.getUsersByRoomUuid(roomUuid);
        roomModel.setUserList(userList);

        // Set Room Chat Uuid
        chatUuid = room.get().getChatUuid();
        roomModel.setChatUuid(chatUuid);

        // Set Invited User List
        invitedUserList = roomInviteService.getInvitedUserListByRoomUuid(roomUuid);
        roomModel.setInvitedUserList(invitedUserList);

        return Optional.of(roomModel);
    }


    @Override
    protected JpaRepository<Room, String> getRepository() {
        return roomRepo;
    }

    @Override
    protected String getId(Room entity) {
        return entity.getUuid();
    }

}
