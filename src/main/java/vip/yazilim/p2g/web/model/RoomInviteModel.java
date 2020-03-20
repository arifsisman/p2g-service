package vip.yazilim.p2g.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import vip.yazilim.p2g.web.entity.RoomInvite;
import vip.yazilim.p2g.web.entity.User;

/**
 * @author mustafaarifsisman - 02.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
@Data
@AllArgsConstructor
public class RoomInviteModel {
    private RoomInvite roomInvite;
    private RoomModel roomModel;
    private User inviter;
}
