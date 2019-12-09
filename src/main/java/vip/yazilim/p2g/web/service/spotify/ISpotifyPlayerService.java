package vip.yazilim.p2g.web.service.spotify;

import vip.yazilim.p2g.web.entity.relation.Song;
import vip.yazilim.p2g.web.exception.PlayerException;
import vip.yazilim.p2g.web.exception.QueueException;
import vip.yazilim.p2g.web.exception.RequestException;
import vip.yazilim.spring.core.exception.general.InvalidArgumentException;
import vip.yazilim.spring.core.exception.general.InvalidUpdateException;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;

import java.util.List;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ISpotifyPlayerService {

    List<Song> play(Song song) throws RequestException, PlayerException, DatabaseException, QueueException, InvalidUpdateException, InvalidArgumentException;
    List<Song> startResume(String roomUuid) throws RequestException, DatabaseException, InvalidUpdateException, PlayerException, InvalidArgumentException, QueueException;
    List<Song> pause(String roomUuid) throws RequestException, DatabaseException, InvalidUpdateException, PlayerException, InvalidArgumentException;
    List<Song> next(String roomUuid) throws RequestException, DatabaseException, PlayerException, QueueException, InvalidUpdateException, InvalidArgumentException;
    List<Song> previous(String roomUuid) throws RequestException, DatabaseException, PlayerException, QueueException, InvalidUpdateException, InvalidArgumentException;
    int seek(String roomUuid, Integer ms) throws RequestException, DatabaseException, PlayerException, InvalidArgumentException;
    boolean repeat(String roomUuid) throws RequestException, DatabaseException, PlayerException, InvalidArgumentException, InvalidUpdateException;

}
