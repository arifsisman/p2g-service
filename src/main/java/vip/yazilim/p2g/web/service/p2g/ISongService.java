package vip.yazilim.p2g.web.service.p2g;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import vip.yazilim.p2g.web.constant.SongStatus;
import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.p2g.web.model.SearchModel;
import vip.yazilim.spring.core.exception.general.InvalidArgumentException;
import vip.yazilim.spring.core.exception.general.InvalidUpdateException;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;
import vip.yazilim.spring.core.exception.general.database.DatabaseReadException;
import vip.yazilim.spring.core.service.ICrudService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ISongService extends ICrudService<Song, Long> {

    Song addSongToRoom(Long roomUuid, String songId, String songUri, String songName, Integer durationMs, int votes) throws DatabaseException, InvalidArgumentException;

    Optional<Song> getPausedSong(Long roomUuid) throws DatabaseReadException;

    Optional<Song> getSongByRoomUuidAndStatus(Long roomUuid, SongStatus songStatus) throws DatabaseReadException;
    List<Song> getSongListByRoomUuidAndStatus(Long roomUuid, SongStatus songStatus) throws DatabaseException;

    Optional<Song> getPlayingSong(Long roomUuid) throws DatabaseReadException;
    Optional<Song> getNextSong(Long roomUuid) throws DatabaseReadException;
    Optional<Song> getPreviousSong(Long roomUuid) throws DatabaseReadException;

    // Rest
    List<Song> addSongToRoom(Long roomUuid, SearchModel searchModel) throws DatabaseException, InvalidArgumentException, IOException, SpotifyWebApiException;
    boolean removeSongFromRoom(Long songUuid) throws DatabaseException, InvalidArgumentException;
    boolean deleteRoomSongList(Long roomUuid) throws DatabaseException;
    List<Song> getSongListByRoomUuid(Long roomUuid) throws DatabaseException;

    int upvote(Long songUuid) throws DatabaseException, InvalidArgumentException, InvalidUpdateException;
    int downvote(Long songUuid) throws DatabaseException, InvalidArgumentException, InvalidUpdateException;

}
