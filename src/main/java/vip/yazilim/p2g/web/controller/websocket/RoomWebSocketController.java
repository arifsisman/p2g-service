package vip.yazilim.p2g.web.controller.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import vip.yazilim.p2g.web.constant.RoomStatus;
import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.p2g.web.model.websocket.ChatMessage;
import vip.yazilim.p2g.web.util.SecurityHelper;
import vip.yazilim.p2g.web.util.TimeHelper;

import java.util.List;
import java.util.UUID;

/**
 * @author mustafaarifsisman - 24.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Controller
public class RoomWebSocketController {

    private Logger LOGGER = LoggerFactory.getLogger(RoomWebSocketController.class);

    @Autowired
    private MessageSendingOperations<String> messagingTemplate;

    //triggers after subscribe request
    @SubscribeMapping("/room/{roomUuid}/messages")
    public void subscribeToRoomMessages(@DestinationVariable UUID roomUuid, Authentication authentication) {
        UUID userUuid = SecurityHelper.getUserUuid(authentication);
        String userDisplayName = SecurityHelper.getUserDisplayName(authentication);

        LOGGER.info("{}[{}] subscribed to roomUuid[{}] messages", userDisplayName, userUuid, roomUuid);
        ChatMessage joinMessage = new ChatMessage("00000000-0000-0000-0000-000000000000", "info", roomUuid.toString(), userDisplayName + " joined!", TimeHelper.getLocalDateTimeNow());
        sendRoomMessage(roomUuid, joinMessage);
    }

    @MessageMapping("/message/{roomUuid}")
    @SendTo("/room/{roomUuid}/messages")
    public ChatMessage sendRoomMessage(@Payload ChatMessage chatMessage) {
        return chatMessage;
    }

    public void sendRoomMessage(@DestinationVariable UUID roomUuid, @Payload ChatMessage chatMessage) {
        messagingTemplate.convertAndSend("/room/" + roomUuid + "/messages", chatMessage);
    }

    public void sendRoomSongList(@DestinationVariable UUID roomUuid, @Payload List<Song> songList) {
        messagingTemplate.convertAndSend("/room/" + roomUuid + "/songs", songList);
    }

    public void sendRoomStatus(@DestinationVariable UUID roomUuid, @Payload RoomStatus roomStatus) {
        messagingTemplate.convertAndSend("/room/" + roomUuid + "/status", roomStatus);
    }

}