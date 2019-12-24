package vip.yazilim.p2g.web.controller.websocket;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import vip.yazilim.p2g.web.model.Message;
import vip.yazilim.p2g.web.model.OutputMessage;
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

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public OutputMessage send(Message message) throws Exception {
        System.out.println(TimeHelper.getLocalDateTimeNow());
        return new OutputMessage(message.getFrom(), message.getText(), TimeHelper.getLocalDateTimeNow());
    }

//    @MessageMapping("/chat")
//    @SendToUser("/topic/messages")
//    public String processMessageFromClient(@Payload String message, Principal principal) throws Exception {
//        return gson.fromJson(message, Map.class).get("text").toString();
//    }

//    @MessageMapping("/message")
//    @SendToUser("/queue/reply")
//    public String processMessageFromClient(@Payload String message, Principal principal) throws Exception {
//        return gson.fromJson(message, Map.class).get("name").toString();
//    }
//
//    @MessageExceptionHandler
//    @SendToUser("/queue/errors")
//    public String handleException(Throwable exception) {
//        return exception.getMessage();
//    }
}