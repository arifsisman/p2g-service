package vip.yazilim.p2g.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vip.yazilim.p2g.web.entity.Song;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ISongRepo extends JpaRepository<Song, Long> {

    List<Song> findByRoomUuid(UUID roomUuid);
    List<Song> findByRoomUuidOrderByVotesDescQueuedTime(UUID roomUuid);
    List<Song> findByRoomUuidAndSongStatusOrderByVotesDescQueuedTime(UUID roomUuid, String queueStatus);
    Optional<Song> findFirstByRoomUuidAndSongStatusOrderByVotesDescQueuedTime(UUID roomUuid, String queueStatus);
    Optional<Song> findFirstByRoomUuidAndSongStatusOrderByPlayingTimeDesc(UUID roomUuid, String queueStatus);

}
