package vip.yazilim.p2g.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import vip.yazilim.p2g.web.constant.enums.FriendRequestStatus;
import vip.yazilim.p2g.web.entity.FriendRequest;
import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.entity.RoomInvite;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.service.p2g.IRoomInviteService;
import vip.yazilim.p2g.web.service.p2g.IRoomService;
import vip.yazilim.p2g.web.service.p2g.ISongService;
import vip.yazilim.p2g.web.service.p2g.IUserService;
import vip.yazilim.p2g.web.service.p2g.impl.FriendRequestService;
import vip.yazilim.p2g.web.util.TimeHelper;
import vip.yazilim.spring.core.exception.general.InvalidArgumentException;
import vip.yazilim.spring.core.exception.general.InvalidUpdateException;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;

import java.util.Collections;

@Component
public class DataInitializer implements CommandLineRunner {

    private Logger LOGGER = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private IUserService userService;

    @Autowired
    private IRoomService roomService;

    @Autowired
    private IRoomInviteService roomInviteService;

    @Autowired
    private FriendRequestService friendRequestService;

    @Autowired
    private ISongService songService;

    @Override
    public void run(String... args) throws DatabaseException, InvalidArgumentException, InvalidUpdateException {
        User arif = userService.createUser("mustafaarifsisman", "mustafaarifsisman@gmail.com", "Mustafa Arif Sisman", "0");
        User emre = userService.createUser("emresen", "maemresen@gmail.com", "Emre Sen", "0");
        User u2 = userService.createUser("2", "2@gmail.com", "Test User 2", "123");
        User u3 = userService.createUser("3", "3@gmail.com", "Test User 3", "123");
        User u5 = userService.createUser("5", "5@gmail.com", "Test User 5", null);
        User u6 = userService.createUser("6", "6@gmail.com", "Test User 6", null);
        User u4 = userService.createUser("4", "4@gmail.com", "Test User 4", null);
        User u7 = userService.createUser("7", "7@gmail.com", "Test User 7", null);
        User u8 = userService.createUser("8", "8@gmail.com", "Test User 8", null);

        Room testRoom1 = roomService.createRoom(arif.getId(), "Test Room 1", "0");
        Room testRoom2 = roomService.createRoom(u2.getId(), "Test Room 2", "123");
        Room testRoom3 = roomService.createRoom(u3.getId(), "Test Room 3", "123");
        Room testRoom4 = roomService.createRoom(u4.getId(), "Test Room 4", null);
        Room testRoom5 = roomService.createRoom(u5.getId(), "Test Room 5", null);
        Room testRoom6 = roomService.createRoom(u6.getId(), "Test Room 6", null);
        Room testRoom7 = roomService.createRoom(u7.getId(), "Test Room 7", null);
        Room testRoom8 = roomService.createRoom(u8.getId(), "Test Room 8", null);

        Long roomId = testRoom1.getId();
        songService.addSongToRoom(roomId, "4VqPOruhp5EdPBeR92t6lQ", "Uprising", Collections.singletonList("Muse"), 1200000, 0);
//        songService.addSongToRoom(roomId, "0c4IEciLCDdXEhhKxj4ThA", "Madness", Collections.singletonList("Muse"), 1200000, 1);
//        songService.addSongToRoom(roomId, "7ouMYWpwJ422jRcDASZB7P", "Knights of Cydonia", Collections.singletonList("Muse"), 1200000, 2);
//        songService.addSongToRoom(roomId, "2takcwOaAZWiXQijPHIx7B", "Time Is Running Out", Collections.singletonList("Muse"), 1200000, 0);

        FriendRequest friendRequest1 = new FriendRequest();
        friendRequest1.setSenderUserId(u2.getId());
        friendRequest1.setReceiverUserId(arif.getId());
        friendRequest1.setRequestStatus(FriendRequestStatus.ACCEPTED.getFriendRequestStatus());
        friendRequestService.create(friendRequest1);

        FriendRequest friendRequest2 = new FriendRequest();
        friendRequest2.setSenderUserId(arif.getId());
        friendRequest2.setReceiverUserId(u2.getId());
        friendRequest2.setRequestStatus(FriendRequestStatus.WAITING.getFriendRequestStatus());
        friendRequestService.create(friendRequest2);

        u2.setImageUrl("http://clipart-library.com/images_k/transparent-apple/transparent-apple-25.png");
        userService.update(u2);

        u3.setImageUrl("http://clipart-library.com/images/qcBoGnLKi.png");
        userService.update(u3);

        u4.setImageUrl("http://clipart-library.com/image_gallery/n700901.jpg");
        userService.update(u4);

        createRoomInvite(arif, u2, testRoom2);
        createRoomInvite(arif, u3, testRoom3);
        createRoomInvite(arif, u4, testRoom4);

    }

    private void createRoomInvite(User receiver, User inviter, Room testRoom2) throws DatabaseException, InvalidArgumentException {
        RoomInvite roomInvite1 = new RoomInvite();
        roomInvite1.setRoomId(testRoom2.getId());
        roomInvite1.setUserId(receiver.getId());
        roomInvite1.setInviterId(inviter.getId());
        roomInvite1.setInvitationDate(TimeHelper.getLocalDateTimeNow());
        roomInviteService.create(roomInvite1);
    }
}
