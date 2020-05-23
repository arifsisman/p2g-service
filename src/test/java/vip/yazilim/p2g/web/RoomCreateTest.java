package vip.yazilim.p2g.web;

import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.entity.RoomUser;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.model.RoomUserModel;
import vip.yazilim.p2g.web.service.p2g.IRoomService;
import vip.yazilim.p2g.web.service.p2g.IRoomUserService;
import vip.yazilim.p2g.web.service.p2g.IUserService;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class RoomCreateTest {

//    @MockBean
//    private IRoomRepo roomRepo;

    @MockBean
    private IUserService userService;
    @MockBean
    private IRoomUserService roomUserService;

    
    @Autowired
    private IRoomService roomService;

    @Test
    public void case1_userNoRoomCreatedBefore() {

        String userId  = "3";
        Mockito.when(userService.getById("3")).thenReturn(
            Optional.of(
                createUser(userId, "4@gmail.com", "Melinda Gardner")
            )
        );
        
        RoomUserModel melindaRoomUserModel = roomService.createRoom(userId, "Only top 50", null);

        Room room = melindaRoomUserModel.getRoom();
        User user = melindaRoomUserModel.getUser();
        RoomUser roomUser = melindaRoomUserModel.getRoomUser();
        
        Assertions.assertThat(roomService.getRoomByUserId(userId).isPresent()).isEqualTo(true);
    }

    @Test
    public void case2_userAlreadyCreatedRoom(){
        
        // 3 => melinda 
        String userId  = "3";
        Mockito.when(userService.getById("3")).thenReturn(
            Optional.of(
                createUser(userId, "4@gmail.com", "Melinda Gardner")
            )
        );

        Mockito.when(roomUserService.leaveRoom()).thenReturn(true);
        
        
        
        
    }
    
    private User createUser(String id, String email, String username) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setName(username);
        return user;
    }
}
