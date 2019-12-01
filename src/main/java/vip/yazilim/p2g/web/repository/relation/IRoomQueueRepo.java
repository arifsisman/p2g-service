package vip.yazilim.p2g.web.repository.relation;

import org.springframework.data.jpa.repository.JpaRepository;
import vip.yazilim.p2g.web.entity.relation.RoomQueue;

import java.util.List;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IRoomQueueRepo extends JpaRepository<RoomQueue, String> {

    List<RoomQueue> findQueuesByRoomUuid(String roomUuid);

    List<RoomQueue> findQueuesByRoomUuidAndQueueStatus(String roomUuid, String queueStatus);

}
