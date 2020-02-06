package vip.yazilim.p2g.web.model;

import lombok.Data;
import vip.yazilim.p2g.web.entity.FriendRequest;

/**
 * @author mustafaarifsisman - 02.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
@Data
public class FriendRequestModel {
    private FriendRequest friendRequest;
    private UserModel friendRequestUser;
}
