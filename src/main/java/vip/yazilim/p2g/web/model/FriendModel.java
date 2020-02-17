package vip.yazilim.p2g.web.model;

import lombok.Data;
import vip.yazilim.p2g.web.entity.User;

/**
 * @author mustafaarifsisman - 17.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
@Data
public class FriendModel {
    private User user;
    private RoomModelSimplified roomModelSimplified;
}
