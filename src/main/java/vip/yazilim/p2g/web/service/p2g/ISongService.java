package vip.yazilim.p2g.web.service.p2g;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import vip.yazilim.libs.springcore.exception.general.BusinessLogicException;
import vip.yazilim.libs.springcore.exception.general.InvalidArgumentException;
import vip.yazilim.libs.springcore.exception.general.database.DatabaseException;
import vip.yazilim.libs.springcore.exception.general.database.DatabaseReadException;
import vip.yazilim.libs.springcore.service.ICrudService;
import vip.yazilim.p2g.web.constant.enums.SongStatus;
import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.p2g.web.model.SearchModel;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ISongService extends ICrudService<Song, Long> {
    List<Song> getSongListByRoomId(Long roomId) throws DatabaseException;

    Optional<Song> getSongByRoomIdAndStatus(Long roomId, SongStatus songStatus) throws DatabaseReadException;

    Optional<Song> getPausedSong(Long roomId) throws DatabaseReadException;

    Optional<Song> getPlayingSong(Long roomId) throws DatabaseReadException;

    Optional<Song> getNextSong(Long roomId) throws DatabaseReadException;

    Optional<Song> getPreviousSong(Long roomId) throws DatabaseReadException;

    boolean addSongToRoom(Long roomId, List<SearchModel> searchModelList) throws BusinessLogicException, IOException, SpotifyWebApiException;

    boolean removeSongFromRoom(Long songId) throws DatabaseException, SpotifyWebApiException, IOException;

    boolean deleteRoomSongList(Long roomId) throws DatabaseException, SpotifyWebApiException, IOException, InvalidArgumentException;

    int upvote(Long songId) throws DatabaseException, InvalidArgumentException;

    int downvote(Long songId) throws DatabaseException, InvalidArgumentException;
}
