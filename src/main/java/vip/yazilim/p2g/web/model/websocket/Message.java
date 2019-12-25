package vip.yazilim.p2g.web.model.websocket;

import lombok.Data;

/**
 * @author mustafaarifsisman - 24.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Data
public class Message {
    private String sender;
    private String content;
}
