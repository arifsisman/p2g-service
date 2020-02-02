package vip.yazilim.p2g.web.model;

import lombok.Data;
import vip.yazilim.p2g.web.entity.RoomInvite;

import java.util.List;

/**
 * @author mustafaarifsisman - 02.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
@Data
public class RoomInviteModel {
    private List<RoomInvite> roomInvites;
    private List<RoomModel> roomModels;
}
