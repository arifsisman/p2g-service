package vip.yazilim.p2g.web.service;

import vip.yazilim.p2g.web.entity.Playlist;
import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.p2g.web.exception.DatabaseException;

import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IPlaylistService extends ICrudService<Playlist, String> {

    List<Song> getSongsByPlaylistUuid(String playlistUuid) throws DatabaseException;
    Optional<String> getImageUrlByPlaylistUuid(String playlistUuid) throws DatabaseException;

}
