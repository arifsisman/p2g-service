package vip.yazilim.p2g.web.service.p2g.impl.relation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.constant.QueueStatus;
import vip.yazilim.p2g.web.entity.relation.RoomQueue;
import vip.yazilim.p2g.web.model.SearchModel;
import vip.yazilim.p2g.web.repository.relation.IRoomQueueRepo;
import vip.yazilim.p2g.web.service.p2g.IQueueService;
import vip.yazilim.p2g.web.util.DBHelper;
import vip.yazilim.p2g.web.util.SpotifyHelper;
import vip.yazilim.spring.utils.exception.DatabaseException;
import vip.yazilim.spring.utils.service.ACrudServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author mustafaarifsisman - 1.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class RoomQueueService extends ACrudServiceImpl<RoomQueue, String> implements IQueueService {

    // static fields
    private Logger LOGGER = LoggerFactory.getLogger(RoomQueueService.class);

    // injected dependencies
    @Autowired
    private IRoomQueueRepo queueRepo;

    @Override
    protected JpaRepository<RoomQueue, String> getRepository() {
        return queueRepo;
    }

    @Override
    protected String getId(RoomQueue entity) {
        return entity.getUuid();
    }

    @Override
    protected RoomQueue preInsert(RoomQueue entity) {
        entity.setUuid(DBHelper.getRandomUuid());
        return entity;
    }

    @Override
    public List<RoomQueue> getQueueListByRoomUuid(String roomUuid) throws DatabaseException {
        try {
            return queueRepo.findQueuesByRoomUuid(roomUuid);
        } catch (Exception exception) {
            String errorMessage = String.format("An error occurred while getting Queue with roomName[%s]", roomUuid);
            throw new DatabaseException(errorMessage, exception);
        }
    }

    @Override
    public RoomQueue addToQueue(String roomUuid, SearchModel searchModel) throws DatabaseException {
        RoomQueue roomQueue = SpotifyHelper.convertSearchModelToRoomQueue(searchModel);
        roomQueue.setRoomUuid(roomUuid);
        return create(roomQueue);
    }

    @Override
    public boolean removeFromQueue(RoomQueue roomQueue) throws DatabaseException {
        return delete(roomQueue);
    }

    @Override
    public List<RoomQueue> getQueueListByRoomUuidAndStatus(String roomUuid, QueueStatus queueStatus) throws DatabaseException {
        List<RoomQueue> roomQueueList = getQueueListByRoomUuid(roomUuid);
        return roomQueueList.stream().filter(x -> x.getQueueStatus().equals(queueStatus.getQueueStatus())).collect(Collectors.toList());
    }

}
