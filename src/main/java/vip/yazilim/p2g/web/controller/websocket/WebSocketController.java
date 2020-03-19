package vip.yazilim.p2g.web.controller.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import vip.yazilim.p2g.web.config.RoomInfoMessageConfig;
import vip.yazilim.p2g.web.entity.RoomUser;
import vip.yazilim.p2g.web.model.ChatMessage;

/**
 * @author mustafaarifsisman - 24.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Controller
public class WebSocketController {

    private Logger LOGGER = LoggerFactory.getLogger(WebSocketController.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private RoomInfoMessageConfig roomInfoMessageConfig;

    @MessageMapping("/p2g/room/{roomId}/send")
    public void chatMessageMapping(@PathVariable ChatMessage chatMessage) {
        RoomUser roomUser = chatMessage.getRoomUser();
        LOGGER.debug("[{}] sending message to Room[{}]: {}", roomUser.getUserId(), roomUser.getRoomId(), chatMessage.getMessage());
        sendToRoom("messages", roomUser.getRoomId(), chatMessage);
    }

    public void sendInfoToRoom(Long roomId, String message) {
        ChatMessage roomInfoMessage = new ChatMessage(roomInfoMessageConfig.getRoomInfoUser(), message);
        messagingTemplate.convertAndSend("/p2g/room/" + roomId + "/messages", roomInfoMessage);
    }

    public void sendToRoom(String destination, Long roomId, Object payload) {
        messagingTemplate.convertAndSend("/p2g/room/" + roomId + "/" + destination, payload);
    }

    public void sendToUser(String destination, String userId, Object payload) {
        messagingTemplate.convertAndSend("/p2g/user/" + userId + "/" + destination, payload);
    }

}