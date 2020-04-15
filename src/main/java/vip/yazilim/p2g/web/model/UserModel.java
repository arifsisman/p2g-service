package vip.yazilim.p2g.web.model;

import lombok.Data;
import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.entity.RoomUser;
import vip.yazilim.p2g.web.entity.User;

import java.io.Serializable;

/**
 * @author mustafaarifsisman - 26.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Data
public class UserModel implements Serializable {
    private User user;
    private Room room;
    private RoomUser roomUser;
    private String roomOwnerName;
    private int roomUserCount;
    //todo merge with roomModel
}
