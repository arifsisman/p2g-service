package vip.yazilim.p2g.web.controller.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.stereotype.Controller;
import vip.yazilim.p2g.web.entity.relation.RoomInvite;

/**
 * @author mustafaarifsisman - 29.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Controller
public class UserWebSocketController {

    @Autowired
    private MessageSendingOperations<String> messagingTemplate;

    public void sendRoomInvite(RoomInvite roomInvite) {
        messagingTemplate.convertAndSend("/user/" + roomInvite.getUserUuid() + "/invites", roomInvite);
    }
}
