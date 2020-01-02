package vip.yazilim.p2g.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vip.yazilim.p2g.web.entity.Song;

import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ISongRepo extends JpaRepository<Song, String> {

    List<Song> findByRoomUuid(String roomUuid);
    List<Song> findByRoomUuidOrderByVotesDescQueuedTime(String roomUuid);
    List<Song> findByRoomUuidAndSongStatusOrderByVotesDescQueuedTime(String roomUuid, String queueStatus);
    Optional<Song> findFirstByRoomUuidAndSongStatusOrderByVotesDescQueuedTime(String roomUuid, String queueStatus);
    Optional<Song> findFirstByRoomUuidAndSongStatusOrderByPlayingTimeDesc(String roomUuid, String queueStatus);

}
