package vip.yazilim.p2g.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author mustafaarifsisman - 24.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Data
@AllArgsConstructor
public class OutputMessage {
    private String from;
    private String text;
    private LocalDateTime time;
}