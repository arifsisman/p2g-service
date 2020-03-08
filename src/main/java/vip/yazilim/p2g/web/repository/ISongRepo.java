package vip.yazilim.p2g.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vip.yazilim.p2g.web.entity.Song;

import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ISongRepo extends JpaRepository<Song, Long> {

    List<Song> findByRoomId(Long roomId);
    List<Song> findByRoomIdOrderByVotesDescQueuedTime(Long roomId);
    Optional<Song> findFirstByRoomIdAndSongStatusOrderByVotesDescQueuedTime(Long roomId, String queueStatus);
    Optional<Song> findFirstByRoomIdAndSongStatusOrderByPlayingTimeDesc(Long roomId, String queueStatus);

}
