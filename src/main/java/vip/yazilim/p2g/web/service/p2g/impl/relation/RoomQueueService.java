package vip.yazilim.p2g.web.service.p2g.impl.relation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.constant.QueueStatus;
import vip.yazilim.p2g.web.entity.relation.RoomQueue;
import vip.yazilim.p2g.web.exception.QueueException;
import vip.yazilim.p2g.web.model.RoomQueueModel;
import vip.yazilim.p2g.web.model.SearchModel;
import vip.yazilim.p2g.web.repository.relation.IRoomQueueRepo;
import vip.yazilim.p2g.web.service.p2g.relation.IRoomQueueService;
import vip.yazilim.p2g.web.util.DBHelper;
import vip.yazilim.p2g.web.util.SpotifyHelper;
import vip.yazilim.spring.core.exception.InvalidArgumentException;
import vip.yazilim.spring.core.exception.InvalidUpdateException;
import vip.yazilim.spring.core.exception.database.DatabaseException;
import vip.yazilim.spring.core.service.ACrudServiceImpl;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 1.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Transactional
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

    @Override
    public RoomQueueModel getRoomQueueModelByRoomUuid(String roomUuid) {
        RoomQueueModel roomQueueModel = new RoomQueueModel();
        List<RoomQueue> roomQueueList;
        RoomQueue nowPlaying;

        // Set Room Uuid
        roomQueueModel.setRoomUuid(roomUuid);

        // Set RoomQueue
        roomQueueList = getRoomQueueListByRoomUuid(roomUuid);
        roomQueueModel.setRoomQueueList(roomQueueList);

        // Set Now Playing
        nowPlaying = getRoomQueueNowPlaying(roomUuid);
        roomQueueModel.setNowPlaying(nowPlaying);

        return roomQueueModel;
    }

    /////////////////////////////
    // Get Queue By Room or Queue Uuid
    /////////////////////////////
    @Override
    public List<RoomQueue> getRoomQueueListByRoomUuid(String roomUuid) {
        return roomQueueRepo.findByRoomUuidOrderByQueuedTime(roomUuid);
    }

    @Override
    public List<RoomQueue> getRoomQueueListByQueueUuid(String queueUuid) {
        RoomQueue roomQueue = roomQueueRepo.findByUuid(queueUuid);
        return roomQueueRepo.findByRoomUuidOrderByQueuedTime(roomQueue.getRoomUuid());
    }

    /////////////////////////////
    // Control Queue
    /////////////////////////////
    @Override
    public RoomQueue addToRoomQueue(String roomUuid, SearchModel searchModel) throws DatabaseException {
        RoomQueue roomQueue = SpotifyHelper.convertSearchModelToRoomQueue(searchModel);
        roomQueue.setRoomUuid(roomUuid);
        return create(roomQueue);
    }

    @Override
    public RoomQueue addToRoomQueue(String roomUuid, String songId, String songUri, String songName, Long durationMs) throws DatabaseException {
        RoomQueue roomQueue = new RoomQueue();
        roomQueue.setRoomUuid(roomUuid);
        roomQueue.setSongId(songId);
        roomQueue.setSongUri(songUri);
        roomQueue.setSongName(songName);
        roomQueue.setDurationMs(durationMs);
        roomQueue.setQueuedTime(new Date());
        roomQueue.setQueueStatus(QueueStatus.IN_QUEUE.getQueueStatus());

        roomQueue = create(roomQueue);
        //TODO: delete log
        LOGGER.info("queueUuid: {} - songName: {}", roomQueue.getUuid(), songName);

        return roomQueue;
    }

    @Override
    public boolean removeFromRoomQueue(String roomQueueUuid) throws DatabaseException, InvalidArgumentException, QueueException {
        Optional<RoomQueue> roomQueueOpt = getById(roomQueueUuid);

        if (!roomQueueOpt.isPresent()) {
            String err = String.format("Queue[%s] not found", roomQueueUuid);
            throw new QueueException(err);
        }

        return delete(roomQueueOpt.get());
    }

    @Override
    public boolean deleteRoomSongList(String roomUuid) throws DatabaseException {
        List<RoomQueue> roomQueueList = roomQueueRepo.findByRoomUuid(roomUuid);

        for(RoomQueue roomQueue: roomQueueList){
            delete(roomQueue);
        }

        return true;
    }

    /////////////////////////////
    // Get Queue By status
    /////////////////////////////
    @Override
    public List<RoomQueue> getRoomQueueListByRoomUuidAndStatus(String roomUuid, QueueStatus queueStatus) {
        return roomQueueRepo.findByRoomUuidAndQueueStatus(roomUuid, queueStatus.getQueueStatus());
    }

    @Override
    public RoomQueue getRoomQueueNowPlaying(String roomUuid) {
        return roomQueueRepo.findByRoomUuidAndQueueStatusIsContaining(roomUuid, QueueStatus.PLAYING.getQueueStatus());
    }

    @Override
    public RoomQueue getRoomQueueNext(String roomUuid) {
        return roomQueueRepo.findByRoomUuidAndQueueStatusIsContaining(roomUuid, QueueStatus.NEXT.getQueueStatus());
    }

    @Override
    public RoomQueue getRoomQueuePrevious(String roomUuid) {
        return roomQueueRepo.findByRoomUuidAndQueueStatusIsContaining(roomUuid, QueueStatus.PREVIOUS.getQueueStatus());
    }

    @Override
    public RoomQueue getRoomQueuePaused(String roomUuid) {
        return roomQueueRepo.findByRoomUuidAndQueueStatusIsContaining(roomUuid, QueueStatus.PAUSED.getQueueStatus());
    }

    /////////////////////////////
    // Update queue status
    /////////////////////////////
    @Override
    public List<RoomQueue> updateRoomQueueStatus(RoomQueue playing) throws DatabaseException, InvalidUpdateException, InvalidArgumentException {
        String roomUuid = playing.getRoomUuid();
        List<RoomQueue> roomQueueList = getRoomQueueListByRoomUuid(roomUuid);

        int playingIndex = roomQueueList.indexOf(playing);

        if (roomQueueList.size() > 1) {
            boolean prevFlag = true;
            ListIterator<RoomQueue> prevIter = roomQueueList.listIterator(playingIndex);
            while (prevIter.hasPrevious()) {
                if (prevFlag) {
                    updateRoomQueue(prevIter.previous(), QueueStatus.PREVIOUS);
                    prevFlag = false;
                } else {
                    updateRoomQueue(prevIter.previous(), QueueStatus.PLAYED);
                }
            }

            updateRoomQueue(playing, QueueStatus.PLAYING);

            boolean nextFlag = true;
            ListIterator<RoomQueue> nextIter = roomQueueList.listIterator(playingIndex + 1);
            while (nextIter.hasNext()) {
                if (nextFlag) {
                    updateRoomQueue(nextIter.next(), QueueStatus.NEXT);
                    nextFlag = false;
                } else {
                    updateRoomQueue(nextIter.next(), QueueStatus.IN_QUEUE);
                }
            }
        } else if (roomQueueList.size() == 1) {
            updateRoomQueue(playing, QueueStatus.PLAYING);
        }

        return roomQueueList;
    }

    private void updateRoomQueue(RoomQueue roomQueue, QueueStatus queueStatus) throws DatabaseException, InvalidUpdateException, InvalidArgumentException {
        roomQueue.setQueueStatus(queueStatus.getQueueStatus());
        update(roomQueue);
    }

}
