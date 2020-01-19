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
    private String userId;
    private String userName;
    private String roomId;
    private String message;
    private LocalDateTime timestamp;
}
