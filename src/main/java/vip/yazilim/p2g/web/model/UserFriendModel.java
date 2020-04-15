package vip.yazilim.p2g.web.model;

import lombok.Data;

import java.util.List;

/**
 * @author mustafaarifsisman - 09.04.2020
 * @contact mustafaarifsisman@gmail.com
 */
@Data
public class UserFriendModel {
    List<FriendRequestModel> requests;
    List<UserModel> friends;
}
