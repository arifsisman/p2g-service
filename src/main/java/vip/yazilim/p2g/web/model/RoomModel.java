package vip.yazilim.p2g.web.model;

import lombok.Data;
import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.entity.relation.RoomQueue;

import java.util.List;

/**
 * @author mustafaarifsisman - 26.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Data
public class RoomModel {
    private Room room;

    private List<User> userList;
    private List<User> invitedUserList;

    private List<RoomQueue> roomQueueList;

    private String chatUuid;
}
