package vip.yazilim.p2g.web.model;

import lombok.Data;
import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.entity.RoomUser;
import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.p2g.web.entity.User;

import java.io.Serializable;
import java.util.List;

/**
 * @author mustafaarifsisman - 26.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Data
public class RoomModel implements Serializable {
    private Room room;
    private User owner;
    private List<User> userList;
    private List<RoomUser> roomUserList;
    private List<Song> songList;
    private List<User> invitedUserList;
}
