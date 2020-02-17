package vip.yazilim.p2g.web.model;

import lombok.Data;
import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.p2g.web.entity.User;

import java.io.Serializable;

/**
 * @author mustafaarifsisman - 17.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
@Data
public class RoomModelSimplified implements Serializable {
    private Room room;
    private User owner;
    private Song song;
}
