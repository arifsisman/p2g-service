package vip.yazilim.p2g.web.model.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author mustafaarifsisman - 24.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@AllArgsConstructor
@Data
public class ChatMessage {
    private UUID userUuid;
    private String userName;
    private Long roomId;
    private String message;
    private LocalDateTime timestamp;
}
