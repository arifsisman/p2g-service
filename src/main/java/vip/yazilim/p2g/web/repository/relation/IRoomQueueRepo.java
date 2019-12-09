package vip.yazilim.p2g.web.repository.relation;

import org.springframework.data.jpa.repository.JpaRepository;
import vip.yazilim.p2g.web.entity.relation.Song;

import java.util.List;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IRoomQueueRepo extends JpaRepository<Song, String> {

    List<Song> findByRoomUuid(String roomUuid);
    List<Song> findByRoomUuidOrderByVotesDescQueuedTime(String roomUuid);
    List<Song> findByRoomUuidAndQueueStatusOrderByVotesDescQueuedTime(String roomUuid, String queueStatus);

}
