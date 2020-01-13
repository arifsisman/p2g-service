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
import java.util.UUID;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ISongService extends ICrudService<Song, Long> {

    Song addSongToRoom(UUID roomUuid, String songId, String songUri, String songName, Integer durationMs, int votes) throws DatabaseException, InvalidArgumentException;

    Optional<Song> getPausedSong(UUID roomUuid) throws DatabaseReadException;

    Optional<Song> getSongByRoomUuidAndStatus(UUID roomUuid, SongStatus songStatus) throws DatabaseReadException;
    List<Song> getSongListByRoomUuidAndStatus(UUID roomUuid, SongStatus songStatus) throws DatabaseException;

    Optional<Song> getPlayingSong(UUID roomUuid) throws DatabaseReadException;
    Optional<Song> getNextSong(UUID roomUuid) throws DatabaseReadException;
    Optional<Song> getPreviousSong(UUID roomUuid) throws DatabaseReadException;

    // Rest
    List<Song> addSongToRoom(UUID roomUuid, SearchModel searchModel) throws DatabaseException, InvalidArgumentException, IOException, SpotifyWebApiException;
    boolean removeSongFromRoom(Long songId) throws DatabaseException, InvalidArgumentException;
    boolean deleteRoomSongList(UUID roomUuid) throws DatabaseException;
    List<Song> getSongListByRoomUuid(UUID roomUuid) throws DatabaseException;

    int upvote(Long songId) throws DatabaseException, InvalidArgumentException, InvalidUpdateException;
    int downvote(Long songId) throws DatabaseException, InvalidArgumentException, InvalidUpdateException;

}
