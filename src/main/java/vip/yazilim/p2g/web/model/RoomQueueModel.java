package vip.yazilim.p2g.web.model;

import lombok.Data;
import vip.yazilim.p2g.web.entity.relation.RoomQueue;

import java.util.List;

/**
 * @author mustafaarifsisman - 03.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Data
public class RoomQueueModel {
    private String roomUuid;

    private List<RoomQueue> roomQueueList;
    private RoomQueue nowPlaying;
}
