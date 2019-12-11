package vip.yazilim.p2g.web.service.spotify;

import vip.yazilim.p2g.web.entity.relation.Song;
import vip.yazilim.p2g.web.exception.PlayerException;
import vip.yazilim.p2g.web.exception.RequestException;
import vip.yazilim.p2g.web.exception.SongException;
import vip.yazilim.spring.core.exception.general.InvalidArgumentException;
import vip.yazilim.spring.core.exception.general.InvalidUpdateException;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;

import java.util.List;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ISpotifyPlayerService {

    List<Song> play(Song song, int ms) throws RequestException, PlayerException, DatabaseException, SongException, InvalidUpdateException, InvalidArgumentException;
    List<Song> startResume(String roomUuid) throws RequestException, DatabaseException, InvalidUpdateException, PlayerException, InvalidArgumentException, SongException;
    List<Song> pause(String roomUuid) throws RequestException, DatabaseException, InvalidUpdateException, PlayerException, InvalidArgumentException;
    List<Song> next(String roomUuid) throws RequestException, DatabaseException, PlayerException, SongException, InvalidUpdateException, InvalidArgumentException;
    List<Song> previous(String roomUuid) throws RequestException, DatabaseException, PlayerException, SongException, InvalidUpdateException, InvalidArgumentException;
    int seek(String roomUuid, Integer ms) throws RequestException, DatabaseException, PlayerException, InvalidArgumentException;
    boolean repeat(String roomUuid) throws RequestException, DatabaseException, PlayerException, InvalidArgumentException, InvalidUpdateException;

}
