package vip.yazilim.p2g.web.service.p2g.relation;

import vip.yazilim.p2g.web.constant.SongStatus;
import vip.yazilim.p2g.web.entity.relation.Song;
import vip.yazilim.p2g.web.model.SearchModel;
import vip.yazilim.spring.core.exception.general.InvalidArgumentException;
import vip.yazilim.spring.core.exception.general.InvalidUpdateException;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;
import vip.yazilim.spring.core.exception.general.database.DatabaseReadException;
import vip.yazilim.spring.core.service.ICrudService;

import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ISongService extends ICrudService<Song, String> {

    Song addSongToRoom(String roomUuid, String songId, String songUri, String songName, Long durationMs, int votes) throws DatabaseException, InvalidArgumentException;

    Optional<Song> getPausedSong(String roomUuid) throws DatabaseReadException;

    Optional<Song> getSongByRoomUuidAndStatus(String roomUuid, SongStatus songStatus) throws DatabaseReadException;
    List<Song> getSongListByRoomUuidAndStatus(String roomUuid, SongStatus songStatus) throws DatabaseException;

    Optional<Song> getPlayingSong(String roomUuid) throws DatabaseReadException;
    Optional<Song> getNextSong(String roomUuid) throws DatabaseReadException;
    Optional<Song> getPreviousSong(String roomUuid) throws DatabaseReadException;

    // Rest
    List<Song> addSongToRoom(String roomUuid, SearchModel searchModel) throws DatabaseException, InvalidArgumentException;
    boolean removeSongFromRoom(String songUuid) throws DatabaseException, InvalidArgumentException;
    boolean deleteRoomSongList(String roomUuid) throws DatabaseException;
    List<Song> getSongListByRoomUuid(String roomUuid) throws DatabaseException;

    int upvote(String songUuid) throws DatabaseException, InvalidArgumentException, InvalidUpdateException;
    int downvote(String songUuid) throws DatabaseException, InvalidArgumentException, InvalidUpdateException;

}
