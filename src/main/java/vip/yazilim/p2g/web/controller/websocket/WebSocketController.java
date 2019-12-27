package vip.yazilim.p2g.web.controller.websocket;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import vip.yazilim.p2g.web.constant.Privilege;
import vip.yazilim.p2g.web.model.websocket.ChatMessage;
import vip.yazilim.p2g.web.service.p2g.impl.relation.RoomUserService;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;

/**
 * @author mustafaarifsisman - 24.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Controller
public class WebSocketController {

    private Logger LOGGER = LoggerFactory.getLogger(WebSocketController.class);

    private Gson gson = new Gson();

    @Autowired
    private RoomUserService roomUserService;

    @MessageMapping("/chat/{roomUuid}")
    @SendTo("/room/{roomUuid}/messages")
    public ChatMessage send(@Payload ChatMessage chatMessage) throws DatabaseException {
        if (roomUserService.hasRoomPrivilege(chatMessage.getUserUuid(), Privilege.ROOM_CHAT)) {
            return chatMessage;
        }
        return null;
    }

    @SubscribeMapping("/room/{roomUuid}/messages")
    public void subscribeToRoom(@DestinationVariable String roomUuid) {
        LOGGER.info("New user subscribed to roomUuid[{}] chat", roomUuid);
    }
}