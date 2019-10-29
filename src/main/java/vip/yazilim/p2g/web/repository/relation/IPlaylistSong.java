package vip.yazilim.p2g.web.repository.relation;

import org.springframework.data.repository.CrudRepository;
import vip.yazilim.p2g.web.entity.relation.PlaylistSong;

import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IPlaylistSong extends CrudRepository<PlaylistSong, String> {

    Optional<PlaylistSong> findByUuid(String uuid);
    Iterable<PlaylistSong> findByPlaylistId(String playlistId);

}
