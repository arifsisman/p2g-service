package vip.yazilim.p2g.web.model;

import lombok.Data;
import vip.yazilim.p2g.web.entity.Role;
import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.entity.relation.UserSettings;

import java.util.List;

@Data
public class UserModel {

    private User user;
    private Room room;
    private Role role;

    private List<User> friends;
    private List<User> friendRequests;

    private UserSettings userSettings;
}
