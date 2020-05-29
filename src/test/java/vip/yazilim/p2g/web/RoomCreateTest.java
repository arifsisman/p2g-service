package vip.yazilim.p2g.web;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.entity.RoomUser;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.model.RoomUserModel;
import vip.yazilim.p2g.web.service.p2g.IRoomService;
import vip.yazilim.p2g.web.service.p2g.IRoomUserService;
import vip.yazilim.p2g.web.service.p2g.IUserService;

import java.util.NoSuchElementException;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class RoomCreateTest {

    private String userId = "wendy";

    @Autowired
    private IUserService userService;

    @Autowired
    private IRoomService roomService;

    @Autowired
    private IRoomUserService roomUserService;

    @Before
    public void createTestUser() {
        User user = new User();
        user.setId(userId);
        user.setEmail("wendy@mail.com");
        user.setName("Wendy Reynolds");

        userService.create(user);
    }

    @Test
    public void case1_userNoRoomCreatedBefore() {
        createTestUser();

        RoomUserModel wendyRoomUserModel = roomService.createRoom(userId, "Only top 50", null);

        Room room = wendyRoomUserModel.getRoom();
        User user = wendyRoomUserModel.getUser();
        RoomUser roomUser = wendyRoomUserModel.getRoomUser();

        Room roomFromService = roomService.getRoomByUserId(userId).orElseThrow(NoSuchElementException::new);
        Assertions.assertThat(room).isSameAs(roomFromService);
    }

//    /**
//     * Test Case -> First create room for user, then create another room for user without leaving the current room.
//     * Check whether the user leaved the room on roomUserService.leaveRoom() triggered.
//     */
//    @Test
//    public void case2_userAlreadyCreatedRoom() {
//    }
}
