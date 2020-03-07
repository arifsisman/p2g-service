package vip.yazilim.p2g.web;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import vip.yazilim.p2g.web.constant.enums.FriendRequestStatus;
import vip.yazilim.p2g.web.constant.enums.OnlineStatus;
import vip.yazilim.p2g.web.constant.enums.Role;
import vip.yazilim.p2g.web.constant.enums.SongStatus;
import vip.yazilim.p2g.web.entity.*;
import vip.yazilim.p2g.web.service.p2g.*;
import vip.yazilim.p2g.web.service.p2g.impl.FriendRequestService;
import vip.yazilim.p2g.web.service.spotify.ISpotifySearchService;
import vip.yazilim.p2g.web.util.TimeHelper;
import vip.yazilim.spring.core.exception.GeneralException;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
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

    @Autowired
    private ISpotifySearchService spotifySearchService;

    @Override
    public void run(String... args) throws GeneralException, IOException, SpotifyWebApiException {
        User arif = userService.createUser("mustafaarifsisman", "mustafaarifsisman@gmail.com", "Mustafa Arif Sisman");
        User emre = userService.createUser("emresen", "maemresen@gmail.com", "Emre Sen");
        User u2 = userService.createUser("2", "2@gmail.com", "Test User 2");
        User u3 = userService.createUser("3", "3@gmail.com", "Test User 3");
        User u4 = userService.createUser("4", "4@gmail.com", "Test User 4");
        User u5 = userService.createUser("5", "5@gmail.com", "Test User 5");
        User u6 = userService.createUser("6", "6@gmail.com", "Test User 6");
        User u7 = userService.createUser("7", "7@gmail.com", "Test User 7");
        User u8 = userService.createUser("8", "8@gmail.com", "Test User 8");

        Room testRoom1 = roomService.createRoom(arif.getId(), "Test Room 1", null);

        Room testRoom2 = roomService.createRoom(u2.getId(), "Test Room 2", "123");
        Room testRoom3 = roomService.createRoom(u3.getId(), "Test Room 3", null);
        Room testRoom4 = roomService.createRoom(u4.getId(), "Test Room 4", null);

        Long roomId = testRoom1.getId();
        addSongToRoom(roomId, "4VqPOruhp5EdPBeR92t6lQ", "Uprising", Collections.singletonList("Muse"), 304840, 0, "https://i.scdn.co/image/ab67616d0000b273b6d4566db0d12894a1a3b7a2");
        addSongToRoom(roomId, "0c4IEciLCDdXEhhKxj4ThA", "Madness", Collections.singletonList("Muse"), 281040, 1, "https://i.scdn.co/image/ab67616d0000b273fc192c54d1823a04ffb6c8c9");
        addSongToRoom(roomId, "7ouMYWpwJ422jRcDASZB7P", "Knights of Cydonia", Collections.singletonList("Muse"), 366213, 2, "https://i.scdn.co/image/ab67616d0000b27328933b808bfb4cbbd0385400");
        addSongToRoom(roomId, "2takcwOaAZWiXQijPHIx7B", "Time Is Running Out", Collections.singletonList("Muse"), 237039, 0, "https://i.scdn.co/image/ab67616d0000b2738cb690f962092fd44bbe2bf4");

        u2.setImageUrl("https://randomuser.me/api/portraits/men/47.jpg");
        userService.update(u2);

        u3.setImageUrl("https://randomuser.me/api/portraits/women/40.jpg");
        userService.update(u3);

        u4.setImageUrl("https://randomuser.me/api/portraits/men/42.jpg");
        userService.update(u4);

        u4.setOnlineStatus(OnlineStatus.AWAY.getOnlineStatus());
        userService.update(u4);

        u5.setOnlineStatus(OnlineStatus.OFFLINE.getOnlineStatus());
        userService.update(u5);

        createRoomInvite(u2, arif, testRoom2);
        createRoomInvite(u3, arif, testRoom3);
        createRoomInvite(u4, arif, testRoom4);

        createFriendRequest(u2, arif, FriendRequestStatus.WAITING);
        createFriendRequest(u3, arif, FriendRequestStatus.WAITING);
        createFriendRequest(u4, arif, FriendRequestStatus.WAITING);
        createFriendRequest(u5, arif, FriendRequestStatus.WAITING);
        createFriendRequest(u6, arif, FriendRequestStatus.ACCEPTED);
        createFriendRequest(u7, arif, FriendRequestStatus.ACCEPTED);
        createFriendRequest(u8, arif, FriendRequestStatus.ACCEPTED);

        roomUserService.joinRoom(testRoom2.getId(), u6.getId(), "123", Role.ROOM_USER);
        roomUserService.joinRoom(testRoom2.getId(), u7.getId(), "123", Role.ROOM_USER);

        roomUserService.joinRoom(testRoom3.getId(), u8.getId(), "''", Role.ROOM_USER);
        roomUserService.joinRoom(testRoom3.getId(), u5.getId(), "", Role.ROOM_USER);
    }

    private void createRoomInvite(User inviter, User receiver, Room testRoom2) throws GeneralException {
        RoomInvite roomInvite = new RoomInvite();
        roomInvite.setRoomId(testRoom2.getId());
        roomInvite.setInviterId(inviter.getId());
        roomInvite.setReceiverId(receiver.getId());
        roomInvite.setInvitationDate(TimeHelper.getLocalDateTimeNow());
        roomInviteService.create(roomInvite);
    }

    private void createFriendRequest(User sender, User receiver, FriendRequestStatus status) throws GeneralException {
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setSenderId(sender.getId());
        friendRequest.setReceiverId(receiver.getId());
        friendRequest.setRequestStatus(status.getFriendRequestStatus());
        friendRequest.setRequestDate(TimeHelper.getLocalDateTimeNow());
        friendRequestService.create(friendRequest);
    }

    private Song addSongToRoom(Long roomId, String songId, String songName, List<String> artistNames, Integer durationMs, int votes, String imageUrl) throws GeneralException {
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
