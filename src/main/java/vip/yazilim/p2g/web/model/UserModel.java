package vip.yazilim.p2g.web.model;

import lombok.Data;
import vip.yazilim.p2g.web.entity.Role;
import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.entity.User;

import java.util.List;

@Data
public class UserModel {
    User user;
    Room room;
    Role role;

    List<User> friends;
    List<User> friendRequests;
}
