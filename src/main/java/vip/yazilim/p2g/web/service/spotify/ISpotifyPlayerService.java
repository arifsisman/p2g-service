package vip.yazilim.p2g.web.service.spotify;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import vip.yazilim.p2g.web.entity.RoomUser;
import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.spring.core.exception.general.InvalidArgumentException;
import vip.yazilim.spring.core.exception.general.InvalidUpdateException;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;

import java.io.IOException;
import java.util.List;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ISpotifyPlayerService {

    List<Song> roomPlay(Song song, int ms) throws DatabaseException, InvalidUpdateException, InvalidArgumentException, IOException, SpotifyWebApiException;
    List<Song> roomStartResume(Long roomUuid) throws DatabaseException, InvalidUpdateException, InvalidArgumentException, IOException, SpotifyWebApiException;
    List<Song> roomPause(Long roomUuid) throws DatabaseException, InvalidUpdateException, InvalidArgumentException, IOException, SpotifyWebApiException;
    List<Song> roomNext(Long roomUuid) throws DatabaseException, InvalidUpdateException, InvalidArgumentException, IOException, SpotifyWebApiException;
    List<Song> roomPrevious(Long roomUuid) throws DatabaseException, InvalidUpdateException, InvalidArgumentException, IOException, SpotifyWebApiException;
    int roomSeek(Long roomUuid, Integer ms) throws DatabaseException, InvalidArgumentException, IOException, SpotifyWebApiException;
    boolean roomRepeat(Long roomUuid) throws DatabaseException, InvalidArgumentException, InvalidUpdateException, IOException, SpotifyWebApiException;

    boolean userSyncWithRoom(RoomUser roomUser) throws DatabaseException, IOException, SpotifyWebApiException;
}
