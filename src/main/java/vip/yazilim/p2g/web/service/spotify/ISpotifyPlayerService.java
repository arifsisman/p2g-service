package vip.yazilim.p2g.web.service.spotify;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import vip.yazilim.p2g.web.entity.relation.Song;
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

    List<Song> play(Song song, int ms) throws DatabaseException, InvalidUpdateException, InvalidArgumentException, IOException, SpotifyWebApiException;
    List<Song> startResume(String roomUuid) throws DatabaseException, InvalidUpdateException, InvalidArgumentException, IOException, SpotifyWebApiException;
    List<Song> pause(String roomUuid) throws DatabaseException, InvalidUpdateException, InvalidArgumentException, IOException, SpotifyWebApiException;
    List<Song> next(String roomUuid) throws DatabaseException, InvalidUpdateException, InvalidArgumentException, IOException, SpotifyWebApiException;
    List<Song> previous(String roomUuid) throws DatabaseException, InvalidUpdateException, InvalidArgumentException, IOException, SpotifyWebApiException;
    int seek(String roomUuid, Integer ms) throws DatabaseException, InvalidArgumentException, IOException, SpotifyWebApiException;
    boolean repeat(String roomUuid) throws DatabaseException, InvalidArgumentException, InvalidUpdateException, IOException, SpotifyWebApiException;

}
