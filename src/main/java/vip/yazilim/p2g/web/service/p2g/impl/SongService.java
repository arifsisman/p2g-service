package vip.yazilim.p2g.web.service.p2g.impl;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.constant.enums.SearchType;
import vip.yazilim.p2g.web.constant.enums.SongStatus;
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
import java.util.Collections;
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
    public List<Song> addSongToRoom(Long roomId, List<SearchModel> searchModel) throws DatabaseException, InvalidArgumentException, IOException, SpotifyWebApiException {
        List<Song> songList = new LinkedList<>();
        for (SearchModel s : searchModel) {
            if (s.getType() == SearchType.TRACK) {
                songList.addAll(getSongListFromSearchModelList(roomId, Collections.singletonList(s)));
            } else if (s.getType() == SearchType.ALBUM) {
                List<SearchModel> searchModelList = spotifyAlbumService.getSongs(s.getId());
                songList.addAll(getSongListFromSearchModelList(roomId, searchModelList));
            } else if (s.getType() == SearchType.PLAYLIST) {
                List<SearchModel> searchModelList = spotifyPlaylistService.getSongs(s.getId());
                songList.addAll(getSongListFromSearchModelList(roomId, searchModelList));
            }
        }

        return songList;
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

    private List<Song> getSongListFromSearchModelList(Long roomId, List<SearchModel> searchModelList) throws DatabaseException, InvalidArgumentException {
        List<Song> songList = new LinkedList<>();

        for (SearchModel s : searchModelList) {
            Song song = new Song();
            song.setRoomId(roomId);
            song.setSongId(s.getId());
            song.setSongName(s.getName());
            song.setAlbumName(s.getAlbumName());
            song.setArtistNames(s.getArtistNames());
            song.setImageUrl(s.getImageUrl());
            song.setCurrentMs(0);
            song.setDurationMs(s.getDurationMs());
            song.setQueuedTime(TimeHelper.getLocalDateTimeNow());
            song.setVotes(0);
            song.setSongStatus(SongStatus.NEXT.getSongStatus());
            songList.add(create(song));
        }

        return songList;
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
