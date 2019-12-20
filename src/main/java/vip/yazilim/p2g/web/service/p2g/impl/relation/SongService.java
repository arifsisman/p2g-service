package vip.yazilim.p2g.web.service.p2g.impl.relation;

import com.wrapper.spotify.enums.ModelObjectType;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.constant.SongStatus;
import vip.yazilim.p2g.web.entity.relation.Song;
import vip.yazilim.p2g.web.model.SearchModel;
import vip.yazilim.p2g.web.repository.relation.ISongRepo;
import vip.yazilim.p2g.web.service.p2g.relation.ISongService;
import vip.yazilim.p2g.web.service.spotify.impl.SpotifyAlbumService;
import vip.yazilim.p2g.web.service.spotify.impl.SpotifyPlaylistService;
import vip.yazilim.p2g.web.util.DBHelper;
import vip.yazilim.p2g.web.util.TimeHelper;
import vip.yazilim.spring.core.exception.general.InvalidArgumentException;
import vip.yazilim.spring.core.exception.general.InvalidUpdateException;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;
import vip.yazilim.spring.core.exception.general.database.DatabaseReadException;
import vip.yazilim.spring.core.exception.web.NotFoundException;
import vip.yazilim.spring.core.service.ACrudServiceImpl;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 1.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Transactional
@Service
public class SongService extends ACrudServiceImpl<Song, String> implements ISongService {

    // static fields
    private Logger LOGGER = LoggerFactory.getLogger(SongService.class);

    // injected dependencies
    @Autowired
    private ISongRepo songRepo;

    @Autowired
    private SpotifyAlbumService spotifyAlbumService;

    @Autowired
    private SpotifyPlaylistService spotifyPlaylistService;

    @Override
    protected JpaRepository<Song, String> getRepository() {
        return songRepo;
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

    @Override
    public List<Song> getSongListByRoomUuid(String roomUuid) {
        // order by votes and queued time
        return songRepo.findByRoomUuidOrderByVotesDescQueuedTime(roomUuid);
    }

    @Override
    public int upvote(String songUuid) throws DatabaseException, InvalidArgumentException, InvalidUpdateException {
        return updateVote(songUuid, true);
    }

    @Override
    public int downvote(String songUuid) throws DatabaseException, InvalidArgumentException, InvalidUpdateException {
        return updateVote(songUuid, false);
    }

    /////////////////////////////
    // Control Queue
    /////////////////////////////
    @Override
    public List<Song> addSongToRoom(String roomUuid, SearchModel searchModel) throws DatabaseException, InvalidArgumentException, IOException, SpotifyWebApiException {
        return convertSearchModelToSong(roomUuid, searchModel);
    }

    //TODO: delete method, this method is test purposes
    @Override
    public Song addSongToRoom(String roomUuid, String songId, String songUri, String songName, Integer durationMs, int votes) throws DatabaseException, InvalidArgumentException {
        Song song = new Song();
        song.setRoomUuid(roomUuid);
        song.setSongId(songId);
        song.setSongUri(songUri);
        song.setSongName(songName);
        song.setDurationMs(durationMs);
        song.setQueuedTime(TimeHelper.getLocalDateTimeNow());
        song.setSongStatus(SongStatus.NEXT.getSongStatus());
        song.setVotes(votes);

        song = create(song);

        LOGGER.info("songUuid: {} - songName: {}", song.getUuid(), songName);

        return song;
    }

    @Override
    public boolean removeSongFromRoom(String songUuid) throws DatabaseException, InvalidArgumentException {
        Optional<Song> SongOpt = getById(songUuid);

        if (!SongOpt.isPresent()) {
            String err = String.format("Song[%s] not found", songUuid);
            throw new NotFoundException(err);
        }

        return delete(SongOpt.get());
    }

    @Override
    public boolean deleteRoomSongList(String roomUuid) throws DatabaseException {
        List<Song> songList = songRepo.findByRoomUuid(roomUuid);

        for (Song song : songList) {
            delete(song);
        }

        return true;
    }

    @Override
    public List<Song> getSongListByRoomUuidAndStatus(String roomUuid, SongStatus songStatus) throws DatabaseReadException {
        try {
            return songRepo.findByRoomUuidAndSongStatusOrderByVotesDescQueuedTime(roomUuid, songStatus.getSongStatus());
        } catch (Exception e) {
            String err = String.format("Database error. Songs cannot found with roomUuid[%s] and status[%s]", roomUuid, songStatus);
            throw new DatabaseReadException(err, e);
        }
    }

    @Override
    public Optional<Song> getSongByRoomUuidAndStatus(String roomUuid, SongStatus songStatus) throws DatabaseReadException {
        try {
            if (songStatus.equals(SongStatus.PLAYED)) {
                return songRepo.findFirstByRoomUuidAndSongStatusOrderByPlayingTimeDesc(roomUuid, songStatus.getSongStatus());
            } else {
                return songRepo.findFirstByRoomUuidAndSongStatusOrderByVotesDescQueuedTime(roomUuid, songStatus.getSongStatus());
            }
        } catch (Exception e) {
            String err = String.format("Database error. Song cannot found with roomUuid[%s] and status[%s]", roomUuid, songStatus);
            throw new DatabaseReadException(err, e);
        }
    }

    @Override
    public Optional<Song> getPlayingSong(String roomUuid) throws DatabaseReadException {
        return getSongByRoomUuidAndStatus(roomUuid, SongStatus.PLAYING);
    }

    @Override
    public Optional<Song> getNextSong(String roomUuid) throws DatabaseReadException {
        return getSongByRoomUuidAndStatus(roomUuid, SongStatus.NEXT);
    }

    @Override
    public Optional<Song> getPreviousSong(String roomUuid) throws DatabaseReadException {
        return getSongByRoomUuidAndStatus(roomUuid, SongStatus.PLAYED);
    }

    @Override
    public Optional<Song> getPausedSong(String roomUuid) throws DatabaseReadException {
        return getSongByRoomUuidAndStatus(roomUuid, SongStatus.PAUSED);
    }

    private List<Song> convertSearchModelToSong(String roomUuid, SearchModel searchModel) throws DatabaseException, InvalidArgumentException, IOException, SpotifyWebApiException {
        List<Song> songList = new LinkedList<>();

        if (searchModel.getType() == ModelObjectType.TRACK) {
            songList.add(getSongFromTrack(roomUuid, searchModel));
        } else if (searchModel.getType() == ModelObjectType.ALBUM) {
            List<SearchModel> searchModelList = spotifyAlbumService.getSongs(searchModel.getId());
            for (SearchModel s : searchModelList) {
                songList.add(getSongFromTrack(roomUuid, s));
            }
        } else {
            List<SearchModel> searchModelList = spotifyPlaylistService.getSongs(searchModel.getId());
            for (SearchModel s : searchModelList) {
                songList.add(getSongFromTrack(roomUuid, s));
            }
        }

        return songList;
    }

    private Song getSongFromTrack(String roomUuid, SearchModel searchModel) throws DatabaseException, InvalidArgumentException {
        Song song = new Song();

        song.setRoomUuid(roomUuid);
        song.setSongId(searchModel.getId());
        song.setSongUri(searchModel.getUri());
        song.setSongName(searchModel.getName());
        song.setAlbumName(searchModel.getAlbumName());
        song.setImageUrl(searchModel.getImageUrl());
        song.setCurrentMs(0);
        song.setDurationMs(searchModel.getDurationMs());
        song.setQueuedTime(TimeHelper.getLocalDateTimeNow());
        song.setVotes(0);
        song.setSongStatus(SongStatus.NEXT.getSongStatus());

        ArtistSimplified[] artists = searchModel.getArtists();
        String[] SongArtists = new String[artists.length];

        for (int i = 0; i < artists.length; i++) {
            SongArtists[i] = artists[i].getName();
        }

        song.setArtists(SongArtists);
        return create(song);
    }

    private Song getSafeSong(String songUuid) throws DatabaseException, InvalidArgumentException {
        Optional<Song> songOpt = getById(songUuid);
        return songOpt.orElseThrow(() -> new NotFoundException("Song not found"));
    }

    private int updateVote(String songUuid, boolean upvote) throws DatabaseException, InvalidArgumentException, InvalidUpdateException {
        Song song = getSafeSong(songUuid);
        int votes = song.getVotes();

        votes = (upvote) ? votes + 1 : votes - 1;

        song.setVotes(votes);
        update(song);

        return votes;
    }

}
