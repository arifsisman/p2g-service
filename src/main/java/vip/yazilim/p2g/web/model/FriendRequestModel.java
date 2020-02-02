package vip.yazilim.p2g.web.model;

import lombok.Data;
import vip.yazilim.p2g.web.entity.FriendRequest;
import vip.yazilim.p2g.web.entity.User;

import java.util.List;

/**
 * @author mustafaarifsisman - 02.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
@Data
public class FriendRequestModel {
    private List<FriendRequest> friendRequests;
    private List<User> friendRequestUsers;
    private List<UserModel> friends;
}
