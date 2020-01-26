package vip.yazilim.p2g.web.model;

import lombok.Data;
import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.entity.RoomUser;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.entity.UserDevice;

import java.io.Serializable;
import java.util.List;

/**
 * @author mustafaarifsisman - 26.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Data
public class UserModel implements Serializable {
    private User user;
    private Room room;
    private List<UserDevice> userDevices;
    private RoomUser roomUser;
    private List<User> friends;
    private List<User> friendRequests;
}
