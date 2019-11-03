package vip.yazilim.p2g.web.repository.relation;

import org.springframework.data.jpa.repository.JpaRepository;
import vip.yazilim.p2g.web.entity.relation.AlbumSong;

import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IAlbumSongRepo extends JpaRepository<AlbumSong, String> {

    Optional<AlbumSong> findByUuid(String uuid);
    List<AlbumSong> findByAlbumUuid(String albumUuid);

}
