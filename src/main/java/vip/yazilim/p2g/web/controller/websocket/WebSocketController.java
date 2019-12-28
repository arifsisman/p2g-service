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
import vip.yazilim.p2g.web.entity.relation.Song;
import vip.yazilim.p2g.web.model.websocket.ChatMessage;
import vip.yazilim.p2g.web.service.p2g.impl.relation.RoomUserService;
import vip.yazilim.p2g.web.util.SecurityHelper;
import vip.yazilim.p2g.web.util.TimeHelper;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;

import java.util.List;

/**
 * @author mustafaarifsisman - 24.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Controller
public class WebSocketController {

    private Logger LOGGER = LoggerFactory.getLogger(WebSocketController.class);

    private Gson gson = new Gson();

    @Autowired
    private MessageSendingOperations<String> messagingTemplate;

    @Autowired
    private RoomUserService roomUserService;

    /////////////////////////////
    // Message send and subscribe
    /////////////////////////////
    @MessageMapping("/message/{roomUuid}")
    @SendTo("/room/{roomUuid}/messages")
    public ChatMessage send(@Payload ChatMessage chatMessage, Authentication authentication) throws DatabaseException {
        if (roomUserService.hasRoomPrivilege(SecurityHelper.getUserUuid(authentication), Privilege.ROOM_CHAT)) {
            return chatMessage;
        }
        return null;
    }

    //triggers after subscribe request
    @SubscribeMapping("/room/{roomUuid}/messages")
    public void subscribeToRoom(@DestinationVariable String roomUuid, Authentication authentication) throws DatabaseException {
        String userUuid = SecurityHelper.getUserUuid(authentication);
        String userDisplayName = SecurityHelper.getUserDisplayName(authentication);

        //todo: bug: not working
        if(!isUserInRoom(userUuid, roomUuid)) return;

        LOGGER.info("{}[{}] subscribed to roomUuid[{}] chat", userDisplayName, userUuid, roomUuid);
        ChatMessage joinMessage = new ChatMessage("-1", "info", roomUuid, userDisplayName + " joined!", TimeHelper.getLocalDateTimeNow());
        messagingTemplate.convertAndSend("/room/" + roomUuid + "/messages", joinMessage);
    }

    /////////////////////////////
    // Song List send
    /////////////////////////////
    @MessageMapping("/song/{roomUuid}")
    @SendTo("/room/{roomUuid}/songs")
    public void updateSongList(@DestinationVariable String roomUuid, @Payload List<Song> songList) {
        messagingTemplate.convertAndSend("/room/" + roomUuid + "/messages", songList);
    }

    private boolean isUserInRoom(String userUuid, String roomUuid) throws DatabaseException {
        return roomUserService.getRoomUser(roomUuid , userUuid).isPresent();
    }
}