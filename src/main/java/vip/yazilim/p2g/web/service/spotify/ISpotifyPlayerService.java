package vip.yazilim.p2g.web.service.spotify;

import vip.yazilim.p2g.web.entity.relation.Song;
import vip.yazilim.p2g.web.exception.SpotifyException;
import vip.yazilim.spring.core.exception.general.InvalidArgumentException;
import vip.yazilim.spring.core.exception.general.InvalidUpdateException;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;

import java.util.List;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ISpotifyPlayerService {

    List<Song> play(Song song, int ms) throws SpotifyException, DatabaseException, InvalidUpdateException, InvalidArgumentException;
    List<Song> startResume(String roomUuid) throws SpotifyException, DatabaseException, InvalidUpdateException, InvalidArgumentException;
    List<Song> pause(String roomUuid) throws SpotifyException, DatabaseException, InvalidUpdateException, InvalidArgumentException;
    List<Song> next(String roomUuid) throws SpotifyException, DatabaseException, InvalidUpdateException, InvalidArgumentException;
    List<Song> previous(String roomUuid) throws SpotifyException, DatabaseException, InvalidUpdateException, InvalidArgumentException;
    int seek(String roomUuid, Integer ms) throws SpotifyException, DatabaseException, InvalidArgumentException;
    boolean repeat(String roomUuid) throws SpotifyException, DatabaseException, InvalidArgumentException, InvalidUpdateException;

}
