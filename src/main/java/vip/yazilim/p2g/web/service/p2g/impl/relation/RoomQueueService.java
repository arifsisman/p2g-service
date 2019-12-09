package vip.yazilim.p2g.web.service.p2g.impl.relation;

import com.wrapper.spotify.enums.ModelObjectType;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.constant.QueueStatus;
import vip.yazilim.p2g.web.entity.relation.Song;
import vip.yazilim.p2g.web.exception.QueueException;
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
public class RoomQueueService extends ACrudServiceImpl<Song, String> implements IRoomQueueService {

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
    protected JpaRepository<Song, String> getRepository() {
        return roomQueueRepo;
    }

    @Override
    protected String getId(Song entity) {
        return entity.getUuid();
    }

    @Override
    protected Song preInsert(Song entity) {
        entity.setUuid(DBHelper.getRandomUuid());
        return entity;
    }

    /////////////////////////////
    // Get Queue By Room or Queue Uuid
    /////////////////////////////
    @Override
    public List<Song> getRoomQueueListByRoomUuid(String roomUuid) {
        // order by votes and queued time
        return roomQueueRepo.findByRoomUuidOrderByVotesDescQueuedTime(roomUuid);
    }

    /////////////////////////////
    // Control Queue
    /////////////////////////////
    @Override
    public List<Song> addToRoomQueue(String roomUuid, SearchModel searchModel) throws DatabaseException {
        return convertSearchModelToRoomQueue(roomUuid, searchModel);
    }

    //TODO: delete method, this method is test purposes
    @Override
    public Song addToRoomQueue(String roomUuid, String songId, String songUri, String songName, Long durationMs, int votes) throws DatabaseException {
        Song song = new Song();
        song.setRoomUuid(roomUuid);
        song.setSongId(songId);
        song.setSongUri(songUri);
        song.setSongName(songName);
        song.setDurationMs(durationMs);
        song.setQueuedTime(new Date());
        song.setQueueStatus(QueueStatus.NEXT.getQueueStatus());
        song.setVotes(votes);

        song = create(song);

        LOGGER.info("queueUuid: {} - songName: {}", song.getUuid(), songName);

        return song;
    }

    @Override
    public boolean removeFromRoomQueue(String roomQueueUuid) throws DatabaseException, InvalidArgumentException, QueueException {
        Optional<Song> roomQueueOpt = getById(roomQueueUuid);

        if (!roomQueueOpt.isPresent()) {
            String err = String.format("Queue[%s] not found", roomQueueUuid);
            throw new QueueException(err);
        }

        return delete(roomQueueOpt.get());
    }

    @Override
    public boolean deleteRoomSongList(String roomUuid) throws DatabaseException {
        List<Song> songList = roomQueueRepo.findByRoomUuid(roomUuid);

        for (Song song : songList) {
            delete(song);
        }

        return true;
    }

    /////////////////////////////
    // Get Queue By status
    /////////////////////////////
    @Override
    public List<Song> getRoomQueueListByRoomUuidAndStatus(String roomUuid, QueueStatus queueStatus) {
        return roomQueueRepo.findByRoomUuidAndQueueStatusOrderByVotesDescQueuedTime(roomUuid, queueStatus.getQueueStatus());
        //TODO: .. database exception
    }

    private Optional<Song> getSong(String roomUuid, QueueStatus queueStatus) {
        List<Song> songList = getRoomQueueListByRoomUuidAndStatus(roomUuid, queueStatus);
        if (!songList.isEmpty()) {
            return Optional.of(songList.get(0));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Song> getRoomQueueNowPlaying(String roomUuid) {
        return getSong(roomUuid, QueueStatus.PLAYING);
    }

    @Override
    public Optional<Song> getRoomQueueNext(String roomUuid) {
        // not get now playing
        // change with order by votes
        return getSong(roomUuid, QueueStatus.NEXT);
    }

    @Override
    public Optional<Song> getRoomQueuePrevious(String roomUuid) {
        return getSong(roomUuid, QueueStatus.PREVIOUS);
    }

    @Override
    public Optional<Song> getRoomQueuePaused(String roomUuid) {
        return getSong(roomUuid, QueueStatus.PAUSED);
    }


    /////////////////////////////
    // Update queue status
    /////////////////////////////
    @Override
    public List<Song> updateRoomQueueStatus(Song playing) throws DatabaseException, InvalidUpdateException, InvalidArgumentException {
        String roomUuid = playing.getRoomUuid();
        List<Song> songList = getRoomQueueListByRoomUuid(roomUuid);

        int playingIndex = songList.indexOf(playing);

        if (songList.size() > 1) {
            boolean prevFlag = true;
            ListIterator<Song> prevIter = songList.listIterator(playingIndex);
            while (prevIter.hasPrevious()) {
                if (prevFlag) {
                    updateRoomQueue(prevIter.previous(), QueueStatus.PREVIOUS);
                    prevFlag = false;
                } else {
                    updateRoomQueue(prevIter.previous(), QueueStatus.PLAYED);
                }
            }

            updateRoomQueue(playing, QueueStatus.PLAYING);

            ListIterator<Song> nextIter = songList.listIterator(playingIndex + 1);
            while (nextIter.hasNext()) {
                updateRoomQueue(nextIter.next(), QueueStatus.NEXT);
            }
        } else if (songList.size() == 1) {
            updateRoomQueue(playing, QueueStatus.PLAYING);
        }

        return songList;
    }

    private void updateRoomQueue(Song song, QueueStatus queueStatus) throws DatabaseException, InvalidUpdateException, InvalidArgumentException {
        song.setQueueStatus(queueStatus.getQueueStatus());
        update(song);
    }

    private List<Song> convertSearchModelToRoomQueue(String roomUuid, SearchModel searchModel) throws DatabaseException {
        List<Song> songList = new LinkedList<>();

        if (searchModel.getType() == ModelObjectType.TRACK) {
            songList.add(getRoomQueueFromTrack(roomUuid, searchModel));
        } else if (searchModel.getType() == ModelObjectType.ALBUM) {
            List<SearchModel> searchModelList = spotifyAlbumService.getSongs(searchModel.getId());
            for (SearchModel s : searchModelList) {
                songList.add(getRoomQueueFromTrack(roomUuid, s));
            }
        } else {
            List<SearchModel> searchModelList = spotifyPlaylistService.getSongs(searchModel.getId());
            for (SearchModel s : searchModelList) {
                songList.add(getRoomQueueFromTrack(roomUuid, s));
            }
        }

        return songList;
    }

    private Song getRoomQueueFromTrack(String roomUuid, SearchModel searchModel) throws DatabaseException {
        Song song = new Song();

        song.setRoomUuid(roomUuid);
        song.setSongId(searchModel.getId());
        song.setSongUri(searchModel.getUri());
        song.setSongName(searchModel.getName());
        song.setAlbumName(searchModel.getAlbumName());
        song.setImageUrl(searchModel.getImageUrl());
        song.setCurrentMs(0L);
        song.setDurationMs(searchModel.getDurationMs());
        song.setQueuedTime(TimeHelper.getCurrentDate());
        song.setVotes(0);
        song.setQueueStatus(QueueStatus.NEXT.getQueueStatus());

        ArtistSimplified[] artists = searchModel.getArtists();
        String[] roomQueueArtists = new String[artists.length];

        for (int i = 0; i < artists.length; i++) {
            roomQueueArtists[i] = artists[i].getName();
        }

        song.setArtists(roomQueueArtists);

        return create(song);
    }

}
