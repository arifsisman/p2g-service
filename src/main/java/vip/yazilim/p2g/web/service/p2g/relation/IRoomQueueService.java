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
public interface IRoomQueueService extends ICrudService<Song, String> {

    List<Song> getRoomQueueListByRoomUuidAndStatus(String roomUuid, QueueStatus queueStatus) throws DatabaseException;
    Song addToRoomQueue(String roomUuid, String songId, String songUri, String songName, Long durationMs, int votes) throws DatabaseException;

    Optional<Song> getRoomQueuePaused(String roomUuid);
    Optional<Song> getRoomQueueNowPlaying(String roomUuid);
    Optional<Song> getRoomQueueNext(String roomUuid);
    Optional<Song> getRoomQueuePrevious(String roomUuid);

    List<Song> updateRoomQueueStatus(Song playing) throws DatabaseException, QueueException, InvalidUpdateException, InvalidArgumentException;

    // Rest
    List<Song> addToRoomQueue(String roomUuid, SearchModel searchModel) throws DatabaseException;
    boolean removeFromRoomQueue(String roomQueueUuid) throws DatabaseException, InvalidArgumentException, QueueException;
    boolean deleteRoomSongList(String roomUuid) throws DatabaseException;
    List<Song> getRoomQueueListByRoomUuid(String roomUuid) throws DatabaseException;

}
