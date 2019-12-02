package vip.yazilim.p2g.web.service.p2g.relation;

import vip.yazilim.p2g.web.constant.QueueStatus;
import vip.yazilim.p2g.web.entity.relation.RoomQueue;
import vip.yazilim.p2g.web.exception.QueueException;
import vip.yazilim.p2g.web.model.SearchModel;
import vip.yazilim.spring.utils.exception.DatabaseException;
import vip.yazilim.spring.utils.exception.InvalidUpdateException;
import vip.yazilim.spring.utils.service.ICrudService;

import java.util.List;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IRoomQueueService extends ICrudService<RoomQueue, String> {

    List<RoomQueue> getQueueListByRoomUuid(String roomUuid) throws DatabaseException;
    List<RoomQueue> getQueueListByQueueUuid(String queueUuid) throws DatabaseException;
    List<RoomQueue> getQueueListByRoomUuidAndStatus(String roomUuid, QueueStatus queueStatus) throws DatabaseException;

    RoomQueue addToQueue(String roomUuid, SearchModel searchModel) throws DatabaseException;
    boolean removeFromQueue(RoomQueue roomQueue) throws DatabaseException;

    RoomQueue getRoomQueueNowPlaying(String roomUuid);
    RoomQueue getRoomQueueNext(String roomUuid);
    RoomQueue getRoomQueuePrevious(String roomUuid);

    List<RoomQueue> updateQueueStatus(RoomQueue playing) throws DatabaseException, QueueException, InvalidUpdateException;

}
