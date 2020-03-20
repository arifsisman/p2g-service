package vip.yazilim.p2g.web.service.p2g.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.libs.springcore.exception.DatabaseReadException;
import vip.yazilim.libs.springcore.service.ACrudServiceImpl;
import vip.yazilim.p2g.web.constant.Constants;
import vip.yazilim.p2g.web.controller.WebSocketController;
import vip.yazilim.p2g.web.entity.RoomUser;
import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.p2g.web.enums.SearchType;
import vip.yazilim.p2g.web.enums.SongStatus;
import vip.yazilim.p2g.web.exception.ConstraintViolationException;
import vip.yazilim.p2g.web.model.SearchModel;
import vip.yazilim.p2g.web.repository.ISongRepo;
import vip.yazilim.p2g.web.service.p2g.IRoomUserService;
import vip.yazilim.p2g.web.service.p2g.ISongService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyAlbumService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyPlayerService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyPlaylistService;
import vip.yazilim.p2g.web.util.RoomHelper;
import vip.yazilim.p2g.web.util.SecurityHelper;
import vip.yazilim.p2g.web.util.TimeHelper;

import java.util.*;

/**
 * @author mustafaarifsisman - 1.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class SongService extends ACrudServiceImpl<Song, Long> implements ISongService {

    @Autowired
    private ISongRepo songRepo;

    @Autowired
    private ISpotifyAlbumService spotifyAlbumService;

    @Autowired
    private ISpotifyPlaylistService spotifyPlaylistService;

    @Autowired
    private IRoomUserService roomUserService;

    @Autowired
    private ISpotifyPlayerService spotifyPlayerService;

    @Autowired
    private WebSocketController webSocketController;

    @Override
    protected JpaRepository<Song, Long> getRepository() {
        return songRepo;
    }

    @Override
    protected Long getId(Song entity) {
        return entity.getId();
    }

    @Override
    protected Class<Song> getClassOfEntity() {
        return Song.class;
    }

    @Override
    public List<Song> getSongListByRoomId(Long roomId) {
        try {
            // order by votes and queued time
            return songRepo.findByRoomIdOrderByVotesDescQueuedTime(roomId);
        } catch (Exception exception) {
            throw new DatabaseReadException(getClassOfEntity(), exception, roomId);
        }
    }

    @Override
    public int upvote(Long songId) {
        return updateVote(songId, true);
    }

    @Override
    public int downvote(Long songId) {
        return updateVote(songId, false);
    }

    @Override
    public boolean addSongToRoom(Long roomId, List<SearchModel> searchModel) {
        List<Song> currentList = getSongListByRoomId(roomId);
        int currentSongCount = (int) currentList.stream().filter(song -> !song.getSongStatus().equals(SongStatus.PLAYED.getSongStatus())).count();
        int remainingSongCount = Constants.ROOM_SONG_LIMIT - currentSongCount;

        List<Song> songList = new LinkedList<>();
        for (SearchModel s : searchModel) {
            if (s.getType() == SearchType.SONG) {
                songList.addAll(getSongListFromSearchModelList(roomId, Collections.singletonList(s)));
            } else if (s.getType() == SearchType.ALBUM) {
                List<SearchModel> searchModelList = spotifyAlbumService.getSongs(s.getId());
                songList.addAll(getSongListFromSearchModelList(roomId, searchModelList));
            } else if (s.getType() == SearchType.PLAYLIST) {
                List<SearchModel> searchModelList = spotifyPlaylistService.getSongs(s.getId());
                songList.addAll(getSongListFromSearchModelList(roomId, searchModelList));
            }
        }

        List<Song> queuedList = new LinkedList<>();

        for (Song s : songList) {
            if (remainingSongCount > 0) {
                queuedList.add(create(s));
                remainingSongCount--;
            } else {
                String err = String.format("Max song limit is %s for rooms.", Constants.ROOM_SONG_LIMIT);
                throw new ConstraintViolationException(err);
            }
        }

        String userName = SecurityHelper.getUserDisplayName();
        String infoMessage = userName + " queued " + queuedList.size() + " songs.\n" + RoomHelper.getQueuedSongNames(queuedList);
        webSocketController.sendInfoToRoom(roomId, infoMessage);

        return true;
    }

    @Override
    public boolean removeSongFromRoom(Long songId) {
        String userId = SecurityHelper.getUserId();
        Optional<Song> songOpt;
        Optional<Song> nowPlayingOpt;
        Optional<RoomUser> roomUserOpt;

        try {
            roomUserOpt = roomUserService.getRoomUserByUserId(userId);
            songOpt = getById(songId);
        } catch (Exception exception) {
            throw new DatabaseReadException(getClassOfEntity(), exception, songId);
        }

        if (!songOpt.isPresent()) {
            String err = String.format("Song[%s] not found", songId);
            throw new NoSuchElementException(err);
        }

        if (!roomUserOpt.isPresent()) {
            String err = String.format("RoomUser not found with UserId[%s]", userId);
            throw new NoSuchElementException(err);
        }

        Long roomId = roomUserOpt.get().getRoomId();

        try {
            nowPlayingOpt = getPlayingSong(roomId);
        } catch (Exception exception) {
            throw new DatabaseReadException(getClassOfEntity(), exception, songId);
        }

        if (nowPlayingOpt.isPresent() && nowPlayingOpt.get().getId().equals(songId)) {
            spotifyPlayerService.roomPlayPause(roomId);
        }

        String userName = SecurityHelper.getUserDisplayName();
        String infoMessage = userName + " removed '" + songOpt.get().toString() + "' from queue.";
        webSocketController.sendInfoToRoom(roomId, infoMessage);

        return delete(songOpt.get());
    }

    @Override
    public boolean deleteRoomSongList(Long roomId) {
        List<Song> songList;

        try {
            songList = songRepo.findByRoomId(roomId);
        } catch (Exception exception) {
            throw new DatabaseReadException(getClassOfEntity(), exception, roomId);
        }

        for (Song song : songList) {
            delete(song);
        }

        return spotifyPlayerService.roomStop(roomId);
    }

    @Override
    public Optional<Song> getSongByRoomIdAndStatus(Long roomId, SongStatus songStatus) throws DatabaseReadException {
        try {
            if (songStatus.equals(SongStatus.PLAYED)) {
                return songRepo.findFirstByRoomIdAndSongStatusOrderByPlayingTimeDesc(roomId, songStatus.getSongStatus());
            } else {
                return getSongListByRoomId(roomId).stream().filter(song -> song.getSongStatus().equals(songStatus.getSongStatus())).findFirst();
            }
        } catch (Exception exception) {
            throw new DatabaseReadException(getClassOfEntity(), exception, roomId, songStatus);
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

    private List<Song> getSongListFromSearchModelList(Long roomId, List<SearchModel> searchModelList) {
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
            songList.add(song);
        }

        return songList;
    }

    private Song getSafeSong(Long songId) {
        Optional<Song> songOpt = getById(songId);
        return songOpt.orElseThrow(() -> new NoSuchElementException("Song not found"));
    }

    private int updateVote(Long songId, boolean upvote) {
        Song song = getSafeSong(songId);
        int votes = song.getVotes();
        String operation;

        votes = (upvote) ? votes + 1 : votes - 1;
        operation = (upvote) ? " upvoted " : " downvoted ";

        song.setVotes(votes);
        update(song);

        String userName = SecurityHelper.getUserDisplayName();
        String infoMessage = userName + operation + "'" + song.toString() + "'";
        webSocketController.sendInfoToRoom(song.getRoomId(), infoMessage);

        return votes;
    }

}
