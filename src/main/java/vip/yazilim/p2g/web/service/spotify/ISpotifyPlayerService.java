package vip.yazilim.p2g.web.service.spotify;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import vip.yazilim.p2g.web.entity.RoomUser;
import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.spring.core.exception.InvalidArgumentException;
import vip.yazilim.spring.core.exception.database.DatabaseException;

import java.io.IOException;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ISpotifyPlayerService {

    boolean roomPlay(Song song, int ms) throws DatabaseException, InvalidArgumentException, IOException, SpotifyWebApiException;
    boolean roomStartResume(Long roomId) throws DatabaseException, InvalidArgumentException, IOException, SpotifyWebApiException;
    boolean roomPause(Long roomId) throws DatabaseException, InvalidArgumentException, IOException, SpotifyWebApiException;
    boolean roomNext(Long roomId) throws DatabaseException, InvalidArgumentException, IOException, SpotifyWebApiException;
    boolean roomPrevious(Long roomId) throws DatabaseException, InvalidArgumentException, IOException, SpotifyWebApiException;
    boolean roomSeek(Long roomId, Integer ms) throws DatabaseException, InvalidArgumentException, IOException, SpotifyWebApiException;
    boolean roomRepeat(Long roomId) throws DatabaseException, InvalidArgumentException, IOException, SpotifyWebApiException;

    boolean userSyncWithRoom(RoomUser roomUser) throws DatabaseException, IOException, SpotifyWebApiException;
}
