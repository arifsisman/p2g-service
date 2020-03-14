package vip.yazilim.p2g.web.service.spotify;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import vip.yazilim.libs.springcore.exception.general.InvalidArgumentException;
import vip.yazilim.libs.springcore.exception.general.InvalidUpdateException;
import vip.yazilim.libs.springcore.exception.general.database.DatabaseException;
import vip.yazilim.p2g.web.entity.RoomUser;
import vip.yazilim.p2g.web.entity.Song;

import java.io.IOException;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ISpotifyPlayerService {

    boolean roomPlay(Song song, int ms, boolean checkCurrent) throws DatabaseException, InvalidArgumentException, IOException, SpotifyWebApiException, InvalidUpdateException;

    boolean roomPlayPause(Long roomId) throws DatabaseException, InvalidArgumentException, IOException, SpotifyWebApiException, InvalidUpdateException;

    boolean roomNext(Long roomId) throws DatabaseException, InvalidArgumentException, IOException, SpotifyWebApiException, InvalidUpdateException;

    boolean roomPrevious(Long roomId) throws DatabaseException, InvalidArgumentException, IOException, SpotifyWebApiException, InvalidUpdateException;

    boolean roomSeek(Long roomId, Integer ms) throws DatabaseException, InvalidArgumentException, IOException, SpotifyWebApiException, InvalidUpdateException;

    boolean roomRepeat(Long roomId) throws DatabaseException, InvalidArgumentException, IOException, SpotifyWebApiException, InvalidUpdateException;

    boolean userSyncWithRoom(RoomUser roomUser) throws DatabaseException, IOException, SpotifyWebApiException;

    boolean roomStop(Long roomId) throws DatabaseException, InvalidArgumentException, IOException, SpotifyWebApiException;

    boolean userDeSyncWithRoom(RoomUser roomUser) throws DatabaseException, IOException, SpotifyWebApiException;

}
