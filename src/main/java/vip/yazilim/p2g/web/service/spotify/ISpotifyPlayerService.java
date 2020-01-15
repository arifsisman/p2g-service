package vip.yazilim.p2g.web.service.spotify;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import vip.yazilim.p2g.web.entity.RoomUser;
import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.spring.core.exception.general.InvalidArgumentException;
import vip.yazilim.spring.core.exception.general.InvalidUpdateException;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ISpotifyPlayerService {

    List<Song> roomPlay(Song song, int ms) throws DatabaseException, InvalidUpdateException, InvalidArgumentException, IOException, SpotifyWebApiException;
    List<Song> roomStartResume(UUID roomUuid) throws DatabaseException, InvalidUpdateException, InvalidArgumentException, IOException, SpotifyWebApiException;
    List<Song> roomPause(UUID roomUuid) throws DatabaseException, InvalidUpdateException, InvalidArgumentException, IOException, SpotifyWebApiException;
    List<Song> roomNext(UUID roomUuid) throws DatabaseException, InvalidUpdateException, InvalidArgumentException, IOException, SpotifyWebApiException;
    List<Song> roomPrevious(UUID roomUuid) throws DatabaseException, InvalidUpdateException, InvalidArgumentException, IOException, SpotifyWebApiException;
    int roomSeek(UUID roomUuid, Integer ms) throws DatabaseException, InvalidArgumentException, IOException, SpotifyWebApiException;
    boolean roomRepeat(UUID roomUuid) throws DatabaseException, InvalidArgumentException, InvalidUpdateException, IOException, SpotifyWebApiException;

    boolean userSyncWithRoom(RoomUser roomUser) throws DatabaseException, IOException, SpotifyWebApiException;
}
