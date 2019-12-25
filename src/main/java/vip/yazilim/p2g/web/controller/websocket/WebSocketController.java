package vip.yazilim.p2g.web.controller.websocket;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import vip.yazilim.p2g.web.model.websocket.ChatMessage;
import vip.yazilim.p2g.web.model.websocket.Message;
import vip.yazilim.p2g.web.util.TimeHelper;

/**
 * @author mustafaarifsisman - 24.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Controller
public class WebSocketController {

    private Logger LOGGER = LoggerFactory.getLogger(WebSocketController.class);

    private Gson gson = new Gson();

    @MessageMapping("/chat/{roomUuid}")
    @SendTo("/room/{roomUuid}/messages")
    public ChatMessage send(@DestinationVariable String roomUuid, @Payload Message message) {
        return new ChatMessage(message.getSender(), message.getContent(), TimeHelper.getLocalDateTimeNow());
    }

    @SubscribeMapping("/room/{roomUuid}/messages")
    public void subscribeToRouteLocation(@DestinationVariable String roomUuid) {
        LOGGER.debug("New user subscribed to roomUuid[{}] chat", roomUuid);
    }
}