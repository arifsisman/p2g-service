package vip.yazilim.p2g.web.controller.websocket;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
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

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    private Gson gson = new Gson();

    @MessageMapping("/{roomUuid}/chat")
    @SendTo("/room/messages")
//    public ChatMessage send(@DestinationVariable String roomUuid, @Payload Message message) {
    public ChatMessage send(Message message) {
        ChatMessage chatMessage = new ChatMessage(message.getSender(), message.getContent(), TimeHelper.getLocalDateTimeNow());
//        messagingTemplate.convertAndSend(format("/chat/%s", roomUuid), chatMessage);
        return chatMessage;
    }

//    @MessageMapping("/chat/{roomUuid}/join}")
//    public void join(@DestinationVariable String roomUuid, String userUuid) {
////        String userDisplayName = SecurityHelper.getUserDisplayName();
//
//        ChatMessage chatMessage = new ChatMessage();
//        chatMessage.setSender(userUuid);
//        chatMessage.setTimestamp(TimeHelper.getLocalDateTimeNow());
//        chatMessage.setContent(userUuid + " joined!");
//
////        String currentRoomUuid = (String) headerAccessor.getSessionAttributes().put("room_uuid", roomUuid);
////        if (currentRoomUuid != null) {
////            ChatMessage leaveMessage = new ChatMessage();
////            leaveMessage.setChatMessageType(ChatMessage.ChatMessageType.LEAVE);
////            leaveMessage.setSender(chatMessage.getSender());
////            messagingTemplate.convertAndSend(format("/room/%s", currentRoomUuid), leaveMessage);
////        }
////        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
//        messagingTemplate.convertAndSend(format("/chat/%s", roomUuid), chatMessage);
//    }
}