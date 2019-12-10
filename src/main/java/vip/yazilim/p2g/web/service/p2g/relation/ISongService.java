package vip.yazilim.p2g.web.service.p2g.relation;

import vip.yazilim.p2g.web.constant.QueueStatus;
import vip.yazilim.p2g.web.entity.relation.Song;
import vip.yazilim.p2g.web.exception.QueueException;
import vip.yazilim.p2g.web.model.SearchModel;
import vip.yazilim.spring.core.exception.general.InvalidArgumentException;
import vip.yazilim.spring.core.exception.general.InvalidUpdateException;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;
import vip.yazilim.spring.core.service.ICrudService;

import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ISongService extends ICrudService<Song, String> {

    List<Song> getSongListByRoomUuidAndStatus(String roomUuid, QueueStatus queueStatus) throws DatabaseException;
    Song addSongToRoom(String roomUuid, String songId, String songUri, String songName, Long durationMs, int votes) throws DatabaseException;

    Optional<Song> getPausedSong(String roomUuid);
    Optional<Song> getNowPlayingSong(String roomUuid);
    Optional<Song> getNextSong(String roomUuid);
    Optional<Song> getPreviousSong(String roomUuid);

    List<Song> updateSongStatus(Song playing) throws DatabaseException, QueueException, InvalidUpdateException, InvalidArgumentException;

    // Rest
    List<Song> addSongToRoom(String roomUuid, SearchModel searchModel) throws DatabaseException;
    boolean removeSongFromRoom(String songUuid) throws DatabaseException, InvalidArgumentException, QueueException;
    boolean deleteRoomSongList(String roomUuid) throws DatabaseException;
    List<Song> getSongListByRoomUuid(String roomUuid) throws DatabaseException;

}
