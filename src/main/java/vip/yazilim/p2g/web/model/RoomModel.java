package vip.yazilim.p2g.web.model;

import lombok.Data;
import vip.yazilim.p2g.web.entity.relation.RoomQueue;
import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.p2g.web.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 26.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Data
public class RoomModel {
    private Room room;
    private List<RoomQueue> roomQueue;
    private Optional<Song> nowPlaying;
    private List<User> userList;
    private String chatUuid;
    private List<User> invitedUserList;
}
