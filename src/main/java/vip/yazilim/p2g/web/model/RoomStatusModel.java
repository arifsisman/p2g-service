package vip.yazilim.p2g.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import vip.yazilim.p2g.web.enums.RoomStatus;

/**
 * @author mustafaarifsisman - 12.04.2020
 * @contact mustafaarifsisman@gmail.com
 */
@Data
@AllArgsConstructor
public class RoomStatusModel {
    private RoomStatus roomStatus;
    private String reason;
}
