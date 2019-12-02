package vip.yazilim.p2g.web.entity.model;

import lombok.Data;
import vip.yazilim.p2g.web.entity.Role;
import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.entity.User;

import java.util.List;

/**
 * @author mustafaarifsisman - 26.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Data
public class UserModel {

    private User user;
    private Room room;
    private Role role;

    private List<User> friends;
    private List<User> friendRequests;

}
