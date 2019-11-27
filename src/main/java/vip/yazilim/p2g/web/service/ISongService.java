package vip.yazilim.p2g.web.service;

import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.spring.utils.exception.DatabaseException;
import vip.yazilim.spring.utils.service.ICrudService;

import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ISongService extends ICrudService<Song, String> {

    Optional<Song> getSongByName(String songName) throws DatabaseException;
    List<Song> getSongsByRoomUuid(String roomUuid) throws DatabaseException;

    String getRoomCurrentSongUuid(String roomUuid) throws DatabaseException;

    List<Song> getSongsByAlbumUuid(String albumUuid) throws DatabaseException;
    List<Song> getSongsByPlaylistUuid(String playlistUuid) throws DatabaseException;

    Song getSafeSong(String uuid) throws DatabaseException;

}
