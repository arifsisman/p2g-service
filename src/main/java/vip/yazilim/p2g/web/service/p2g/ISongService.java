package vip.yazilim.p2g.web.service.p2g;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import vip.yazilim.p2g.web.constant.enums.SongStatus;
import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.p2g.web.model.SearchModel;
import vip.yazilim.spring.core.exception.GeneralException;
import vip.yazilim.spring.core.exception.InvalidArgumentException;
import vip.yazilim.spring.core.exception.database.DatabaseException;
import vip.yazilim.spring.core.exception.database.DatabaseReadException;
import vip.yazilim.spring.core.service.ICrudService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ISongService extends ICrudService<Song, Long> {
    Optional<Song> getPausedSong(Long roomId) throws DatabaseReadException;

    Optional<Song> getSongByRoomIdAndStatus(Long roomId, SongStatus songStatus) throws DatabaseReadException;
    List<Song> getSongListByRoomIdAndStatus(Long roomId, SongStatus songStatus) throws DatabaseException;

    Optional<Song> getPlayingSong(Long roomId) throws DatabaseReadException;
    Optional<Song> getNextSong(Long roomId) throws DatabaseReadException;
    Optional<Song> getPreviousSong(Long roomId) throws DatabaseReadException;

    // Rest
    List<Song> addSongToRoom(Long roomId, List<SearchModel> searchModelList) throws GeneralException, IOException, SpotifyWebApiException;
    boolean removeSongFromRoom(Long songId) throws DatabaseException;
    boolean deleteRoomSongList(Long roomId) throws DatabaseException;
    List<Song> getSongListByRoomId(Long roomId) throws DatabaseException;

    int upvote(Long songId) throws DatabaseException, InvalidArgumentException;
    int downvote(Long songId) throws DatabaseException, InvalidArgumentException;

}
