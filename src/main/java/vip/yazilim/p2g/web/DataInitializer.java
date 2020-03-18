package vip.yazilim.p2g.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import vip.yazilim.p2g.web.constant.enums.FriendRequestStatus;
import vip.yazilim.p2g.web.constant.enums.OnlineStatus;
import vip.yazilim.p2g.web.constant.enums.Role;
import vip.yazilim.p2g.web.constant.enums.SongStatus;
import vip.yazilim.p2g.web.entity.*;
import vip.yazilim.p2g.web.service.p2g.*;
import vip.yazilim.p2g.web.service.p2g.impl.FriendRequestService;
import vip.yazilim.p2g.web.util.TimeHelper;

import java.util.Collections;
import java.util.List;

@Component
@ConditionalOnProperty(value = "DATA_INIT_FLAG", havingValue = "true")
public class DataInitializer implements CommandLineRunner {

    private Logger LOGGER = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private IUserService userService;

    @Autowired
    private IRoomService roomService;

    @Autowired
    private IRoomUserService roomUserService;

    @Autowired
    private IRoomInviteService roomInviteService;

    @Autowired
    private FriendRequestService friendRequestService;

    @Autowired
    private ISongService songService;

    @Override
    public void run(String... args) {
        User arif = userService.createUser("mustafaarifsisman", "mustafaarifsisman@gmail.com", "Mustafa Arif Sisman");
        User u1 = userService.createUser("1", "2@gmail.com", "Test User 1");
        User u2 = userService.createUser("2", "3@gmail.com", "Test User 2");
        User u3 = userService.createUser("3", "4@gmail.com", "Test User 3");
        User u4 = userService.createUser("4", "5@gmail.com", "Test User 4");
        User u5 = userService.createUser("5", "6@gmail.com", "Test User 5");
        User u6 = userService.createUser("6", "7@gmail.com", "Test User 6");
        User u7 = userService.createUser("7", "8@gmail.com", "Test User 7");
        User emre = userService.createUser("emresen", "maemresen@gmail.com", "Emre Sen");

        Room demoRoom = roomService.createRoom(arif.getId(), "Demo Room", null);
        Room testRoom2 = roomService.createRoom(u1.getId(), "Test Room 1", "123");
        Room testRoom3 = roomService.createRoom(u2.getId(), "Test Room 2", null);
        Room testRoom4 = roomService.createRoom(u3.getId(), "Test Room 3", null);

        Long roomId = demoRoom.getId();
        addSongToRoom(roomId, "4VqPOruhp5EdPBeR92t6lQ", "Uprising", Collections.singletonList("Muse"), 304840, 0, "https://i.scdn.co/image/ab67616d0000b273b6d4566db0d12894a1a3b7a2");
        addSongToRoom(roomId, "0c4IEciLCDdXEhhKxj4ThA", "Madness", Collections.singletonList("Muse"), 281040, 1, "https://i.scdn.co/image/ab67616d0000b273fc192c54d1823a04ffb6c8c9");
        addSongToRoom(roomId, "7ouMYWpwJ422jRcDASZB7P", "Knights of Cydonia", Collections.singletonList("Muse"), 366213, 2, "https://i.scdn.co/image/ab67616d0000b27328933b808bfb4cbbd0385400");
        addSongToRoom(roomId, "2takcwOaAZWiXQijPHIx7B", "Time Is Running Out", Collections.singletonList("Muse"), 237039, 0, "https://i.scdn.co/image/ab67616d0000b2738cb690f962092fd44bbe2bf4");

        u1.setImageUrl("https://randomuser.me/api/portraits/men/47.jpg");
        userService.update(u1);

        u2.setImageUrl("https://randomuser.me/api/portraits/women/40.jpg");
        userService.update(u2);

        u3.setImageUrl("https://randomuser.me/api/portraits/men/42.jpg");
        userService.update(u3);

        u3.setOnlineStatus(OnlineStatus.AWAY.getOnlineStatus());
        userService.update(u3);

        u4.setOnlineStatus(OnlineStatus.OFFLINE.getOnlineStatus());
        userService.update(u4);

        emre.setOnlineStatus(OnlineStatus.AWAY.getOnlineStatus());
        userService.update(emre);

        createRoomInvite(u1, arif, testRoom2);
        createRoomInvite(u2, arif, testRoom3);
        createRoomInvite(u3, arif, testRoom4);

        createFriendRequest(u1, arif, FriendRequestStatus.WAITING);
        createFriendRequest(u2, arif, FriendRequestStatus.WAITING);
        createFriendRequest(u3, arif, FriendRequestStatus.WAITING);
        createFriendRequest(u4, arif, FriendRequestStatus.WAITING);
        createFriendRequest(u5, arif, FriendRequestStatus.ACCEPTED);
        createFriendRequest(u6, arif, FriendRequestStatus.ACCEPTED);
        createFriendRequest(u7, arif, FriendRequestStatus.ACCEPTED);
        createFriendRequest(emre, arif, FriendRequestStatus.ACCEPTED);

        roomUserService.joinRoom(testRoom2.getId(), u5.getId(), "123", Role.ROOM_USER);
        roomUserService.joinRoom(testRoom2.getId(), u6.getId(), "123", Role.ROOM_USER);

        RoomUser r3u8 = roomUserService.joinRoom(testRoom3.getId(), u7.getId(), "''", Role.ROOM_USER);
        RoomUser r3u5 = roomUserService.joinRoom(testRoom3.getId(), u4.getId(), "", Role.ROOM_USER);

        r3u8.setRole(Role.ROOM_ADMIN.role);
        roomUserService.update(r3u8);

        r3u5.setRole(Role.ROOM_DJ.role);
        roomUserService.update(r3u5);
    }

    private void createRoomInvite(User inviter, User receiver, Room testRoom2) {
        RoomInvite roomInvite = new RoomInvite();
        roomInvite.setRoomId(testRoom2.getId());
        roomInvite.setInviterId(inviter.getId());
        roomInvite.setReceiverId(receiver.getId());
        roomInvite.setInvitationDate(TimeHelper.getLocalDateTimeNow());
        roomInviteService.create(roomInvite);
    }

    private void createFriendRequest(User sender, User receiver, FriendRequestStatus status) {
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setSenderId(sender.getId());
        friendRequest.setReceiverId(receiver.getId());
        friendRequest.setRequestStatus(status.getFriendRequestStatus());
        friendRequest.setRequestDate(TimeHelper.getLocalDateTimeNow());
        friendRequestService.create(friendRequest);
    }

    private Song addSongToRoom(Long roomId, String songId, String songName, List<String> artistNames, Integer durationMs, int votes, String imageUrl) {
        Song song = new Song();
        song.setRoomId(roomId);
        song.setSongId(songId);
        song.setSongName(songName);
        song.setArtistNames(artistNames);
        song.setCurrentMs(0);
        song.setDurationMs(durationMs);
        song.setQueuedTime(TimeHelper.getLocalDateTimeNow());
        song.setSongStatus(SongStatus.NEXT.getSongStatus());
        song.setVotes(votes);
        song.setImageUrl(imageUrl);

        song = songService.create(song);

        LOGGER.info("songId: {} - songName: {}", song.getId(), songName);

        return song;
    }
}
