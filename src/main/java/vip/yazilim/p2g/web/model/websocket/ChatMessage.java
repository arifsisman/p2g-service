package vip.yazilim.p2g.web.model.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author mustafaarifsisman - 24.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@AllArgsConstructor
@Data
public class ChatMessage {
    private String sender;
    private String content;
    private LocalDateTime timestamp;
}
