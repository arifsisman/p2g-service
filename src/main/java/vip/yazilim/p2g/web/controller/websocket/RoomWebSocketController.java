package vip.yazilim.p2g.web.controller.websocket;

import com.google.gson.Gson;
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
import vip.yazilim.p2g.web.constant.Privilege;
import vip.yazilim.p2g.web.constant.RoomStatus;
import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.p2g.web.model.websocket.ChatMessage;
import vip.yazilim.p2g.web.service.p2g.impl.RoomUserService;
import vip.yazilim.p2g.web.util.SecurityHelper;
import vip.yazilim.p2g.web.util.TimeHelper;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;

import java.util.List;
import java.util.UUID;

/**
 * @author mustafaarifsisman - 24.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Controller
public class RoomWebSocketController {

    private Logger LOGGER = LoggerFactory.getLogger(RoomWebSocketController.class);

    private Gson gson = new Gson();

    @Autowired
    private MessageSendingOperations<String> messagingTemplate;

    @Autowired
    private RoomUserService roomUserService;

    //triggers after subscribe request
    @SubscribeMapping("/room/{roomId}/messages")
    public void subscribeToRoomMessages(@DestinationVariable Long roomId, Authentication authentication) {
        UUID userUuid = SecurityHelper.getUserUuid(authentication);
        String userDisplayName = SecurityHelper.getUserDisplayName(authentication);

        LOGGER.info("{}[{}] subscribed to roomId[{}] messages", userDisplayName, userUuid, roomId);
        ChatMessage joinMessage = new ChatMessage(UUID.fromString("00000000-0000-0000-0000-000000000000"), "info", roomId, userDisplayName + " joined!", TimeHelper.getLocalDateTimeNow());
        messagingTemplate.convertAndSend("/room/" + roomId + "/messages", joinMessage);
    }

    @MessageMapping("/message/{roomId}")
    @SendTo("/room/{roomId}/messages")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage, Authentication authentication) throws DatabaseException {
        if (roomUserService.hasRoomPrivilege(SecurityHelper.getUserUuid(authentication), Privilege.ROOM_CHAT)) {
            return chatMessage;
        }
        return null;
    }

    public void updateSongList(@DestinationVariable Long roomId, @Payload List<Song> songList) {
        messagingTemplate.convertAndSend("/room/" + roomId + "/songs", songList);
    }

    public void updateRoomStatus(@DestinationVariable Long roomId, @Payload RoomStatus roomStatus) {
        messagingTemplate.convertAndSend("/room/" + roomId + "/status", roomStatus);
    }

    private boolean isUserInRoom(UUID userUuid, Long roomId) throws DatabaseException {
        return roomUserService.getRoomUser(roomId , userUuid).isPresent();
    }

}