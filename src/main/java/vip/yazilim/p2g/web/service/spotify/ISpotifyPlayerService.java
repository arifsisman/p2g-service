package vip.yazilim.p2g.web.service.spotify;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import vip.yazilim.p2g.web.entity.RoomUser;
import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.spring.core.exception.InvalidArgumentException;
import vip.yazilim.spring.core.exception.database.DatabaseException;

import java.io.IOException;
import java.util.List;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ISpotifyPlayerService {

    List<Song> roomPlay(Song song, int ms) throws DatabaseException, InvalidArgumentException, IOException, SpotifyWebApiException;
    List<Song> roomStartResume(Long roomId) throws DatabaseException, InvalidArgumentException, IOException, SpotifyWebApiException;
    List<Song> roomPause(Long roomId) throws DatabaseException, InvalidArgumentException, IOException, SpotifyWebApiException;
    List<Song> roomNext(Long roomId) throws DatabaseException, InvalidArgumentException, IOException, SpotifyWebApiException;
    List<Song> roomPrevious(Long roomId) throws DatabaseException, InvalidArgumentException, IOException, SpotifyWebApiException;
    int roomSeek(Long roomId, Integer ms) throws DatabaseException, InvalidArgumentException, IOException, SpotifyWebApiException;
    boolean roomRepeat(Long roomId) throws DatabaseException, InvalidArgumentException, IOException, SpotifyWebApiException;

    boolean userSyncWithRoom(RoomUser roomUser) throws DatabaseException, IOException, SpotifyWebApiException;
}
