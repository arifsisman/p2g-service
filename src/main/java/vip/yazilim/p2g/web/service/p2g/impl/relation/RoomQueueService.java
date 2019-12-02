package vip.yazilim.p2g.web.service.p2g.impl.relation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.constant.QueueStatus;
import vip.yazilim.p2g.web.entity.model.SearchModel;
import vip.yazilim.p2g.web.entity.relation.RoomQueue;
import vip.yazilim.p2g.web.repository.relation.IRoomQueueRepo;
import vip.yazilim.p2g.web.service.p2g.relation.IRoomQueueService;
import vip.yazilim.p2g.web.util.DBHelper;
import vip.yazilim.p2g.web.util.SpotifyHelper;
import vip.yazilim.spring.utils.exception.DatabaseException;
import vip.yazilim.spring.utils.exception.InvalidUpdateException;
import vip.yazilim.spring.utils.service.ACrudServiceImpl;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * @author mustafaarifsisman - 1.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class RoomQueueService extends ACrudServiceImpl<RoomQueue, String> implements IRoomQueueService {

    // static fields
    private Logger LOGGER = LoggerFactory.getLogger(RoomQueueService.class);

    // injected dependencies
    @Autowired
    private IRoomQueueRepo roomQueueRepo;

    @Override
    protected JpaRepository<RoomQueue, String> getRepository() {
        return roomQueueRepo;
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

    /////////////////////////////
    // Get Queue By Room or Queue Uuid
    /////////////////////////////
    @Override
    public List<RoomQueue> getQueueListByRoomUuid(String roomUuid) {
        return roomQueueRepo.findByRoomUuid(roomUuid);
    }

    @Override
    public List<RoomQueue> getQueueListByQueueUuid(String queueUuid) {
        RoomQueue roomQueue = roomQueueRepo.findByUuid(queueUuid);
        return roomQueueRepo.findByRoomUuid(roomQueue.getRoomUuid());
    }

    /////////////////////////////
    // Control Queue
    /////////////////////////////
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

    /////////////////////////////
    // Get Queue By status
    /////////////////////////////
    @Override
    public List<RoomQueue> getQueueListByRoomUuidAndStatus(String roomUuid, QueueStatus queueStatus) {
        return roomQueueRepo.findByRoomUuidAndQueueStatus(roomUuid, queueStatus.getQueueStatus());
    }

    @Override
    public RoomQueue getRoomQueueNowPlaying(String roomUuid) {
        return roomQueueRepo.findByRoomUuidAndQueueStatusIsContaining(roomUuid, QueueStatus.NOW_PLAYING.getQueueStatus());
    }

    @Override
    public RoomQueue getRoomQueueNext(String roomUuid) {
        return roomQueueRepo.findByRoomUuidAndQueueStatusIsContaining(roomUuid, QueueStatus.NEXT.getQueueStatus());
    }

    @Override
    public RoomQueue getRoomQueuePrevious(String roomUuid) {
        return roomQueueRepo.findByRoomUuidAndQueueStatusIsContaining(roomUuid, QueueStatus.PREVIOUS.getQueueStatus());
    }

    /////////////////////////////
    // Update queue status
    /////////////////////////////
    @Override
    public List<RoomQueue> updateQueueStatus(RoomQueue playing) throws DatabaseException, InvalidUpdateException {
        String roomUuid = playing.getRoomUuid();
        List<RoomQueue> roomQueueList = new LinkedList<>(getQueueListByRoomUuid(roomUuid));

        int playingIndex = roomQueueList.indexOf(playing);

        if (roomQueueList.size() > 1) {
            boolean prevFlag = true;
            ListIterator<RoomQueue> prevIter = roomQueueList.listIterator(playingIndex);
            while (prevIter.hasPrevious()) {
                if (prevFlag) {
                    updateQueue(prevIter.previous(), QueueStatus.PREVIOUS);
                    prevFlag = false;
                } else {
                    updateQueue(prevIter.previous(), QueueStatus.PLAYED);
                }
            }

            updateQueue(playing, QueueStatus.NOW_PLAYING);

            boolean nextFlag = true;
            ListIterator<RoomQueue> nextIter = roomQueueList.listIterator(playingIndex + 1);
            while (nextIter.hasNext()) {
                if (nextFlag) {
                    updateQueue(nextIter.next(), QueueStatus.NEXT);
                    nextFlag = false;
                } else {
                    updateQueue(nextIter.next(), QueueStatus.IN_QUEUE);

                }
            }
        }else if(roomQueueList.size() == 1){
            updateQueue(playing, QueueStatus.NOW_PLAYING);
        }

        return roomQueueList;
    }

    private void updateQueue(RoomQueue roomQueue, QueueStatus queueStatus) throws DatabaseException, InvalidUpdateException {
        roomQueue.setQueueStatus(queueStatus.getQueueStatus());
        update(roomQueue);
    }

}
