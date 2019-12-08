package vip.yazilim.p2g.web.service.p2g.relation;

import vip.yazilim.p2g.web.constant.QueueStatus;
import vip.yazilim.p2g.web.entity.relation.RoomQueue;
import vip.yazilim.p2g.web.exception.QueueException;
import vip.yazilim.p2g.web.model.RoomQueueModel;
import vip.yazilim.p2g.web.model.SearchModel;
import vip.yazilim.spring.core.exception.general.InvalidArgumentException;
import vip.yazilim.spring.core.exception.general.InvalidUpdateException;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;
import vip.yazilim.spring.core.service.ICrudService;

import java.util.List;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IRoomQueueService extends ICrudService<RoomQueue, String> {

    List<RoomQueue> getRoomQueueListByRoomUuidAndStatus(String roomUuid, QueueStatus queueStatus) throws DatabaseException;
    RoomQueue addToRoomQueue(String roomUuid, String songId, String songUri, String songName, Long durationMs) throws DatabaseException;

    RoomQueue getRoomQueueFirstQueued(String roomUuid);
    RoomQueue getRoomQueuePaused(String roomUuid);
    RoomQueue getRoomQueueNowPlaying(String roomUuid);
    RoomQueue getRoomQueueNext(String roomUuid);
    RoomQueue getRoomQueuePrevious(String roomUuid);

    List<RoomQueue> updateRoomQueueStatus(RoomQueue playing) throws DatabaseException, QueueException, InvalidUpdateException, InvalidArgumentException;

    // Rest
    RoomQueue addToRoomQueue(String roomUuid, SearchModel searchModel) throws DatabaseException;
    boolean removeFromRoomQueue(String roomQueueUuid) throws DatabaseException, InvalidArgumentException, QueueException;
    boolean deleteRoomSongList(String roomUuid) throws DatabaseException;
    List<RoomQueue> getRoomQueueListByRoomUuid(String roomUuid) throws DatabaseException;
    RoomQueueModel getRoomQueueModelByRoomUuid(String roomUuid);

}
