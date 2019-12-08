package vip.yazilim.p2g.web.service.p2g.impl.relation;

import com.wrapper.spotify.enums.ModelObjectType;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
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
import vip.yazilim.p2g.web.service.spotify.impl.SpotifyAlbumService;
import vip.yazilim.p2g.web.service.spotify.impl.SpotifyPlaylistService;
import vip.yazilim.p2g.web.util.DBHelper;
import vip.yazilim.p2g.web.util.TimeHelper;
import vip.yazilim.spring.core.exception.general.InvalidArgumentException;
import vip.yazilim.spring.core.exception.general.InvalidUpdateException;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;
import vip.yazilim.spring.core.service.ACrudServiceImpl;

import javax.transaction.Transactional;
import java.util.*;

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

    @Autowired
    private SpotifyAlbumService spotifyAlbumService;

    @Autowired
    private SpotifyPlaylistService spotifyPlaylistService;

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
        // order by votes and queued time
        return roomQueueRepo.findByRoomUuidOrderByVotesDescQueuedTime(roomUuid);
    }

    /////////////////////////////
    // Control Queue
    /////////////////////////////
    @Override
    public List<RoomQueue> addToRoomQueue(String roomUuid, SearchModel searchModel) throws DatabaseException {
        return convertSearchModelToRoomQueue(searchModel);
    }

    //TODO: delete method, this method is test purposes
    @Override
    public RoomQueue addToRoomQueue(String roomUuid, String songId, String songUri, String songName, Long durationMs, int votes) throws DatabaseException {
        RoomQueue roomQueue = new RoomQueue();
        roomQueue.setRoomUuid(roomUuid);
        roomQueue.setSongId(songId);
        roomQueue.setSongUri(songUri);
        roomQueue.setSongName(songName);
        roomQueue.setDurationMs(durationMs);
        roomQueue.setQueuedTime(new Date());
        roomQueue.setQueueStatus(QueueStatus.IN_QUEUE.getQueueStatus());
        roomQueue.setVotes(votes);

        roomQueue = create(roomQueue);

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

        for (RoomQueue roomQueue : roomQueueList) {
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

    @Override
    public RoomQueue getRoomQueueFirstQueued(String roomUuid) {
        return roomQueueRepo.findFirstByRoomUuidOrderByVotesDescQueuedTime(roomUuid);
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

    private List<RoomQueue> convertSearchModelToRoomQueue(SearchModel searchModel) throws DatabaseException {
        List<RoomQueue> roomQueueList = new LinkedList<>();

        if (searchModel.getType() == ModelObjectType.TRACK) {
            roomQueueList.add(getRoomQueueFromTrack(searchModel));
        } else if (searchModel.getType() == ModelObjectType.ALBUM) {
            List<SearchModel> searchModelList = spotifyAlbumService.getSongs(searchModel.getId());
            for (SearchModel s : searchModelList) {
                roomQueueList.add(getRoomQueueFromTrack(s));
            }
        } else {
            List<SearchModel> searchModelList = spotifyPlaylistService.getSongs(searchModel.getId());
            for (SearchModel s : searchModelList) {
                roomQueueList.add(getRoomQueueFromTrack(s));
            }
        }

        return roomQueueList;
    }

    private RoomQueue getRoomQueueFromTrack(SearchModel searchModel) throws DatabaseException {
        RoomQueue roomQueue = new RoomQueue();

        roomQueue.setSongId(searchModel.getId());
        roomQueue.setSongUri(searchModel.getUri());
        roomQueue.setSongName(searchModel.getName());
        roomQueue.setAlbumName(searchModel.getAlbumName());
        roomQueue.setImageUrl(searchModel.getImageUrl());
        roomQueue.setCurrentMs(0L);
        roomQueue.setDurationMs(searchModel.getDurationMs());
        roomQueue.setQueuedTime(TimeHelper.getCurrentDate());
        roomQueue.setVotes(0);
        roomQueue.setQueueStatus(QueueStatus.IN_QUEUE.getQueueStatus());

        ArtistSimplified[] artists = searchModel.getArtists();
        String[] roomQueueArtists = new String[artists.length];

        for (int i = 0; i < artists.length; i++) {
            roomQueueArtists[i] = artists[i].getName();
        }

        roomQueue.setArtists(roomQueueArtists);

        return create(roomQueue);
    }

}
