package vip.yazilim.p2g.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import vip.yazilim.p2g.web.entity.RoomUser;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * @author mustafaarifsisman - 24.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@AllArgsConstructor
@Data
public class ChatMessage implements Serializable {
    private RoomUser roomUser;
    private String message;
    private LocalDateTime timestamp;

    public ChatMessage(RoomUser roomUser) {
        this.roomUser = roomUser;
    }
}
