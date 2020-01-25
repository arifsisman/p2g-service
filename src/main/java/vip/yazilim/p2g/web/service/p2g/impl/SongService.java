package vip.yazilim.p2g.web.service.p2g.impl;

import com.wrapper.spotify.enums.ModelObjectType;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.constant.SongStatus;
import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.p2g.web.model.SearchModel;
import vip.yazilim.p2g.web.repository.ISongRepo;
import vip.yazilim.p2g.web.service.p2g.ISongService;
import vip.yazilim.p2g.web.service.spotify.impl.SpotifyAlbumService;
import vip.yazilim.p2g.web.service.spotify.impl.SpotifyPlaylistService;
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
public class SongService extends ACrudServiceImpl<Song, Long> implements ISongService {

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
    protected JpaRepository<Song, Long> getRepository() {
        return songRepo;
    }

    @Override
    protected Long getId(Song entity) {
        return entity.getId();
    }

    @Override
    public List<Song> getSongListByRoomId(Long roomId) {
        // order by votes and queued time
        return songRepo.findByRoomIdOrderByVotesDescQueuedTime(roomId);
    }

    @Override
    public int upvote(Long songId) throws DatabaseException, InvalidArgumentException, InvalidUpdateException {
        return updateVote(songId, true);
    }

    @Override
    public int downvote(Long songId) throws DatabaseException, InvalidArgumentException, InvalidUpdateException {
        return updateVote(songId, false);
    }

    @Override
    public List<Song> addSongToRoom(Long roomId, SearchModel searchModel) throws DatabaseException, InvalidArgumentException, IOException, SpotifyWebApiException {
        return convertSearchModelToSong(roomId, searchModel);
    }

    //TODO: delete method, this method is test purposes
    @Override
    public Song addSongToRoom(Long roomId, String songId, String songUri, String songName, Integer durationMs, int votes) throws DatabaseException, InvalidArgumentException {
        Song song = new Song();
        song.setRoomId(roomId);
        song.setSongId(songId);
        song.setSongUri(songUri);
        song.setSongName(songName);
        song.setDurationMs(durationMs);
        song.setQueuedTime(TimeHelper.getDateTimeNow());
        song.setSongStatus(SongStatus.NEXT.getSongStatus());
        song.setVotes(votes);

        song = create(song);

        LOGGER.info("songId: {} - songName: {}", song.getId(), songName);

        return song;
    }

    @Override
    public boolean removeSongFromRoom(Long songId) throws DatabaseException, InvalidArgumentException {
        Optional<Song> SongOpt = getById(songId);

        if (!SongOpt.isPresent()) {
            String err = String.format("Song[%s] not found", songId);
            throw new NotFoundException(err);
        }

        return delete(SongOpt.get());
    }

    @Override
    public boolean deleteRoomSongList(Long roomId) throws DatabaseException {
        List<Song> songList = songRepo.findByRoomId(roomId);

        for (Song song : songList) {
            delete(song);
        }

        return true;
    }

    @Override
    public List<Song> getSongListByRoomIdAndStatus(Long roomId, SongStatus songStatus) throws DatabaseReadException {
        try {
            return songRepo.findByRoomIdAndSongStatusOrderByVotesDescQueuedTime(roomId, songStatus.getSongStatus());
        } catch (Exception e) {
            String err = String.format("Database error. Songs cannot found with roomId[%s] and status[%s]", roomId, songStatus);
            throw new DatabaseReadException(err, e);
        }
    }

    @Override
    public Optional<Song> getSongByRoomIdAndStatus(Long roomId, SongStatus songStatus) throws DatabaseReadException {
        try {
            if (songStatus.equals(SongStatus.PLAYED)) {
                return songRepo.findFirstByRoomIdAndSongStatusOrderByPlayingTimeDesc(roomId, songStatus.getSongStatus());
            } else {
                return songRepo.findFirstByRoomIdAndSongStatusOrderByVotesDescQueuedTime(roomId, songStatus.getSongStatus());
            }
        } catch (Exception e) {
            String err = String.format("Database error. Song cannot found with roomId[%s] and status[%s]", roomId, songStatus);
            throw new DatabaseReadException(err, e);
        }
    }

    @Override
    public Optional<Song> getPlayingSong(Long roomId) throws DatabaseReadException {
        return getSongByRoomIdAndStatus(roomId, SongStatus.PLAYING);
    }

    @Override
    public Optional<Song> getNextSong(Long roomId) throws DatabaseReadException {
        return getSongByRoomIdAndStatus(roomId, SongStatus.NEXT);
    }

    @Override
    public Optional<Song> getPreviousSong(Long roomId) throws DatabaseReadException {
        return getSongByRoomIdAndStatus(roomId, SongStatus.PLAYED);
    }

    @Override
    public Optional<Song> getPausedSong(Long roomId) throws DatabaseReadException {
        return getSongByRoomIdAndStatus(roomId, SongStatus.PAUSED);
    }

    private List<Song> convertSearchModelToSong(Long roomId, SearchModel searchModel) throws DatabaseException, InvalidArgumentException, IOException, SpotifyWebApiException {
        List<Song> songList = new LinkedList<>();

        if (searchModel.getType() == ModelObjectType.TRACK) {
            songList.add(getSongFromTrack(roomId, searchModel));
        } else if (searchModel.getType() == ModelObjectType.ALBUM) {
            List<SearchModel> searchModelList = spotifyAlbumService.getSongs(searchModel.getId());
            for (SearchModel s : searchModelList) {
                songList.add(getSongFromTrack(roomId, s));
            }
        } else {
            List<SearchModel> searchModelList = spotifyPlaylistService.getSongs(searchModel.getId());
            for (SearchModel s : searchModelList) {
                songList.add(getSongFromTrack(roomId, s));
            }
        }

        return songList;
    }

    private Song getSongFromTrack(Long roomId, SearchModel searchModel) throws DatabaseException, InvalidArgumentException {
        Song song = new Song();

        song.setRoomId(roomId);
        song.setSongId(searchModel.getId());
        song.setSongUri(searchModel.getUri());
        song.setSongName(searchModel.getName());
        song.setAlbumName(searchModel.getAlbumName());
        song.setImageUrl(searchModel.getImageUrl());
        song.setCurrentMs(0);
        song.setDurationMs(searchModel.getDurationMs());
        song.setQueuedTime(TimeHelper.getDateTimeNow());
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

    private Song getSafeSong(Long songId) throws DatabaseException, InvalidArgumentException {
        Optional<Song> songOpt = getById(songId);
        return songOpt.orElseThrow(() -> new NotFoundException("Song not found"));
    }

    private int updateVote(Long songId, boolean upvote) throws DatabaseException, InvalidArgumentException, InvalidUpdateException {
        Song song = getSafeSong(songId);
        int votes = song.getVotes();

        votes = (upvote) ? votes + 1 : votes - 1;

        song.setVotes(votes);
        update(song);

        return votes;
    }

}
