package vip.yazilim.p2g.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vip.yazilim.p2g.web.entity.Playlist;

import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IPlaylistRepo extends JpaRepository<Playlist, String> {

    Optional<Playlist> findByUuid(String uuid);
    List<Playlist> findByPlaylistId(String playlistId);

}
