package vip.yazilim.p2g.web.controller.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import vip.yazilim.p2g.web.model.websocket.ChatMessage;
import vip.yazilim.p2g.web.util.SecurityHelper;
import vip.yazilim.p2g.web.util.TimeHelper;

/**
 * @author mustafaarifsisman - 24.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Controller
public class WebSocketController {

    private Logger LOGGER = LoggerFactory.getLogger(WebSocketController.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @SubscribeMapping("/p2g/room/{roomId}")
    public void subscribeToRoomWebSocket(@DestinationVariable Long roomId, OAuth2Authentication oAuth2Authentication) {
        String userId = SecurityHelper.getUserId(oAuth2Authentication);
        String userDisplayName = SecurityHelper.getUserDisplayName(oAuth2Authentication);

        LOGGER.info("{}[{}] subscribed to /p2g/room/{}", userDisplayName, userId, roomId);
        ChatMessage joinMessage = new ChatMessage("-1", "INFO", roomId.toString()
                , userDisplayName + " joined!", TimeHelper.getDateTimeNow());
        sendToRoom("messages", roomId, joinMessage);
    }

    @SubscribeMapping("/p2g/user/{userId}")
    public void subscribeToUserWebSocket(@DestinationVariable String userId, OAuth2Authentication oAuth2Authentication) {
        String userDisplayName = SecurityHelper.getUserDisplayName(oAuth2Authentication);
        LOGGER.info("{}[{}] subscribed to /p2g/user/{}", userDisplayName, userId, userId);
    }

    @MessageMapping("/p2g/room/{roomId}")
    @SendTo("/p2g/room/{roomId}/messages")
    public ChatMessage chatMessageMapping(@PathVariable ChatMessage chatMessage) {
        LOGGER.debug("Received Message: {}", chatMessage.getMessage());
        return chatMessage;
    }

    public void sendToRoom(String destination, Long roomId, Object payload) {
        messagingTemplate.convertAndSend("/p2g/room/" + roomId + "/" + destination, payload);
    }

    public void sendToUser(String destination, String userId, Object payload) {
        messagingTemplate.convertAndSend("/p2g/user/" + userId + "/" + destination, payload);
    }

}