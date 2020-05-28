package vip.yazilim.p2g.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import vip.yazilim.p2g.web.entity.*;
import vip.yazilim.p2g.web.enums.FriendRequestStatus;
import vip.yazilim.p2g.web.enums.OnlineStatus;
import vip.yazilim.p2g.web.enums.Role;
import vip.yazilim.p2g.web.enums.SongStatus;
import vip.yazilim.p2g.web.model.RoomUserModel;
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
        User u1 = createUser("tobias", "tobias@mail.com", "Tobias Schulze");
        User u2 = createUser("wendy", "wendy@mail.com", "Wendy Reynolds");
        User u3 = createUser("melinda", "melinda@mail.com", "Melinda Gardner");
        User u4 = createUser("clara", "clara@mail.com", "Clara Martin");
        User u5 = createUser("austin", "austin@mail.com", "Austin Jimenez");
        User u6 = createUser("carter", "carter@mail.com", "Carter Gonzales");
        User u7 = createUser("edgar", "edgar@mail.com", "Edgar Davidson");
        User u8 = createUser("edward", "edward@mail.com", "Edward Daniels");
        User u9 = createUser("julia", "julia@mail.com", "Julia Kim");
        User arif = createUser("mustafaarifsisman", "mustafaarifsisman@gmail.com", "Mustafa Arif Sisman");
        User emre = createUser("emresen", "maemresen@gmail.com", "Emre Sen");

        RoomUserModel demoRoomUserModel = roomService.createRoom(arif.getId(), "Demo Room", null);
        RoomUserModel testRoom2UserModel = roomService.createRoom(u1.getId(), "My Private Room", "123");
        RoomUserModel testRoom3UserModel = roomService.createRoom(u2.getId(), "Chill", null);
        RoomUserModel testRoom4UserModel = roomService.createRoom(u3.getId(), "Only top 50", null);

        Room demoRoom = demoRoomUserModel.getRoom();
        Room testRoom2 = testRoom2UserModel.getRoom();
        Room testRoom3 = testRoom3UserModel.getRoom();
        Room testRoom4 = testRoom4UserModel.getRoom();

        demoRoom.setActiveFlag(true);
        roomService.update(demoRoom);

        Long roomId = demoRoom.getId();
        addSongToRoom(roomId, "4VqPOruhp5EdPBeR92t6lQ", "Uprising", Collections.singletonList("Muse"), 304840, 0, 0, "https://i.scdn.co/image/ab67616d0000b273b6d4566db0d12894a1a3b7a2", SongStatus.NEXT);
        addSongToRoom(roomId, "0c4IEciLCDdXEhhKxj4ThA", "Madness", Collections.singletonList("Muse"), 281040, 0, 1, "https://i.scdn.co/image/ab67616d0000b273fc192c54d1823a04ffb6c8c9", SongStatus.NEXT);
        addSongToRoom(roomId, "7ouMYWpwJ422jRcDASZB7P", "Knights of Cydonia", Collections.singletonList("Muse"), 366213, 0, 2, "https://i.scdn.co/image/ab67616d0000b27328933b808bfb4cbbd0385400", SongStatus.NEXT);
        addSongToRoom(roomId, "2takcwOaAZWiXQijPHIx7B", "Time Is Running Out", Collections.singletonList("Muse"), 237039, 0, 0, "https://i.scdn.co/image/ab67616d0000b2738cb690f962092fd44bbe2bf4", SongStatus.NEXT);

        addSongToRoom(testRoom2.getId(), "0c4IEciLCDdXEhhKxj4ThA", "Madness", Collections.singletonList("Muse"), 281040, 0, 1, "https://i.scdn.co/image/ab67616d0000b273fc192c54d1823a04ffb6c8c9", SongStatus.PLAYING);
        addSongToRoom(testRoom3.getId(), "4VqPOruhp5EdPBeR92t6lQ", "Uprising", Collections.singletonList("Muse"), 304840, 0, 2, "https://i.scdn.co/image/ab67616d0000b273b6d4566db0d12894a1a3b7a2", SongStatus.PLAYING);

        u1.setImageUrl("https://randomuser.me/api/portraits/men/47.jpg");
        u1.setCountryCode("DE");
        userService.update(u1);

        u2.setImageUrl("https://randomuser.me/api/portraits/women/33.jpg");
        u2.setCountryCode("GB");
        userService.update(u2);

        u4.setImageUrl("https://randomuser.me/api/portraits/men/42.jpg");
        userService.update(u4);

        u5.setImageUrl("https://randomuser.me/api/portraits/men/54.jpg");
        userService.update(u5);

        u5.setImageUrl("https://randomuser.me/api/portraits/men/54.jpg");
        userService.update(u5);

        u6.setImageUrl("https://randomuser.me/api/portraits/men/17.jpg");
        userService.update(u6);

        u9.setImageUrl("https://randomuser.me/api/portraits/women/82.jpg");
        userService.update(u9);

        u3.setOnlineStatus(OnlineStatus.AWAY.getOnlineStatus());
        u3.setCountryCode("TR");
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

        RoomUserModel r3u8UserModel = roomUserService.joinRoom(testRoom3.getId(), u7.getId(), null, Role.ROOM_USER);
        RoomUserModel r3u5UserModel = roomUserService.joinRoom(testRoom3.getId(), u4.getId(), null, Role.ROOM_USER);

        RoomUser r3u8 = r3u8UserModel.getRoomUser();
        RoomUser r3u5 = r3u5UserModel.getRoomUser();

        r3u8.setRoomRole(Role.ROOM_ADMIN.role);
        roomUserService.update(r3u8);

        r3u5.setRoomRole(Role.ROOM_DJ.role);
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

    private Song addSongToRoom(Long roomId, String songId, String songName, List<String> artistNames, Integer durationMs, Integer currentMs, int votes, String imageUrl, SongStatus songStatus) {
        Song song = new Song();
        song.setRoomId(roomId);
        song.setSongId(songId);
        song.setSongName(songName);
        song.setAlbumName("Test");
        song.setRepeatFlag(false);
        song.setArtistNames(artistNames);
        song.setCurrentMs(currentMs);
        song.setDurationMs(durationMs);
        song.setQueuedTime(TimeHelper.getLocalDateTimeNow());
        song.setSongStatus(songStatus.getSongStatus());
        song.setVotes(votes);
        song.setImageUrl(imageUrl);

        if (songStatus.getSongStatus().equals(SongStatus.PLAYING.getSongStatus())) {
            song.setPlayingTime(TimeHelper.getLocalDateTimeNow());
        }

        song = songService.create(song);

        LOGGER.info("songId: {} - songName: {}", song.getId(), songName);

        return song;
    }

    private User createUser(String id, String email, String username) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setName(username);

        return userService.create(user);
    }
}
