package vip.yazilim.p2g.web.model;

import lombok.Data;
import vip.yazilim.p2g.web.entity.Role;
import vip.yazilim.p2g.web.entity.User;

@Data
public class UserModel {
    User user;
    Role role;
}
