package vip.yazilim.p2g.web.controller.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import vip.yazilim.p2g.web.model.websocket.ChatMessage;
import vip.yazilim.p2g.web.util.SecurityHelper;
import vip.yazilim.p2g.web.util.TimeHelper;

import java.util.UUID;

/**
 * @author mustafaarifsisman - 24.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Controller
public class WebSocketController {

    private Logger LOGGER = LoggerFactory.getLogger(WebSocketController.class);

    @Autowired
    private MessageSendingOperations<String> messagingTemplate;

    @SubscribeMapping("/room/{roomUuid}")
    public void subscribeToRoomWebSocket(@DestinationVariable UUID roomUuid, Authentication authentication) {
        UUID userUuid = SecurityHelper.getUserUuid(authentication);
        String userDisplayName = SecurityHelper.getUserDisplayName(authentication);

        LOGGER.info("{}[{}] subscribed to roomUuid[{}]", userDisplayName, userUuid, roomUuid);
        ChatMessage joinMessage = new ChatMessage("-1", "info", roomUuid.toString(), userDisplayName + " joined!", TimeHelper.getLocalDateTimeNow());
        sendToRoom(roomUuid, joinMessage);
    }

    @SubscribeMapping("/user/{userUuid}")
    public void subscribeToUserWebSocket(@DestinationVariable UUID userUuid, Authentication authentication) {
        String userDisplayName = SecurityHelper.getUserDisplayName(authentication);
        LOGGER.info("{}[{}] subscribed to own ws topic", userDisplayName, userUuid);
    }

    public void sendToRoom(UUID roomUuid, Object payload){
        messagingTemplate.convertAndSend("/room/" + roomUuid, payload);
    }

    public void sendToUser(UUID userUuid, Object payload){
        messagingTemplate.convertAndSend("/user/" + userUuid, payload);
    }

}