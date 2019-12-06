package vip.yazilim.p2g.web.repository.relation;

import org.springframework.data.jpa.repository.JpaRepository;
import vip.yazilim.p2g.web.entity.relation.RoomQueue;

import java.util.List;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IRoomQueueRepo extends JpaRepository<RoomQueue, String> {

    RoomQueue findByUuid(String queueUuid);
    List<RoomQueue> findByRoomUuid(String roomUuid);
    List<RoomQueue> findByRoomUuidOrderByQueuedTime(String roomUuid);
    List<RoomQueue> findByRoomUuidAndQueueStatus(String roomUuid, String queueStatus);
    RoomQueue findByRoomUuidAndQueueStatusIsContaining(String roomUuid, String queueStatusNowPlaying);

}
