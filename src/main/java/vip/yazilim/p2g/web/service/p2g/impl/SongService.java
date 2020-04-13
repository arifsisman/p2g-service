package vip.yazilim.p2g.web.service.p2g.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.libs.springcore.exception.DatabaseReadException;
import vip.yazilim.libs.springcore.service.ACrudServiceImpl;
import vip.yazilim.p2g.web.constant.Constants;
import vip.yazilim.p2g.web.controller.WebSocketController;
import vip.yazilim.p2g.web.entity.RoomUser;
import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.p2g.web.enums.RoomStatus;
import vip.yazilim.p2g.web.enums.SearchType;
import vip.yazilim.p2g.web.enums.SongStatus;
import vip.yazilim.p2g.web.exception.ConstraintViolationException;
import vip.yazilim.p2g.web.model.RoomStatusModel;
import vip.yazilim.p2g.web.model.SearchModel;
import vip.yazilim.p2g.web.repository.ISongRepo;
import vip.yazilim.p2g.web.service.p2g.IRoomUserService;
import vip.yazilim.p2g.web.service.p2g.ISongService;
import vip.yazilim.p2g.web.service.spotify.IPlayerService;
import vip.yazilim.p2g.web.service.spotify.ISpotifySearchService;
import vip.yazilim.p2g.web.util.SecurityHelper;
import vip.yazilim.p2g.web.util.TimeHelper;

import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author mustafaarifsisman - 1.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class SongService extends ACrudServiceImpl<Song, Long> implements ISongService {

    @Autowired
    private ISongRepo songRepo;

    @Autowired
    private ISpotifySearchService spotifySearchService;

    @Autowired
    private IRoomUserService roomUserService;

    @Autowired
    private IPlayerService spotifyPlayerService;

    @Autowired
    private WebSocketController webSocketController;

    private Logger LOGGER = LoggerFactory.getLogger(SongService.class);

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

    public static int getCumulativePassedMs(Song song) {
        return (int) (ChronoUnit.MILLIS.between(song.getPlayingTime(), TimeHelper.getLocalDateTimeNow()) + song.getCurrentMs());
    }

    @Override
    public List<Song> getSongListByRoomId(Long roomId, boolean includePlayedSongs) {
        try {
            if (includePlayedSongs) {
                return songRepo.findByRoomIdOrderByVotesDescQueuedTime(roomId);
            } else {
                return songRepo.findByRoomIdAndSongStatusNotOrderBySongStatusDescVotesDescQueuedTime(roomId, SongStatus.PLAYED.getSongStatus());
            }
        } catch (Exception exception) {
            throw new DatabaseReadException(getClassOfEntity(), exception, roomId);
        }
    }

    /**
     * @param roomId     roomId
     * @param songStatus songStatus
     * @return getSongByRoomIdAndStatus not including played songs
     * @throws DatabaseReadException DatabaseReadException
     */
    @Override
    public Optional<Song> getSongByRoomIdAndStatus(Long roomId, SongStatus songStatus) throws DatabaseReadException {
        try {
            return getSongListByRoomId(roomId, false).stream().filter(song -> song.getSongStatus().equals(songStatus.getSongStatus())).findFirst();
        } catch (Exception exception) {
            throw new DatabaseReadException(getClassOfEntity(), exception, roomId, songStatus);
        }
    }

    @Override
    public Optional<Song> getPlayingSong(Long roomId) throws DatabaseReadException {
        return getSongByRoomIdAndStatus(roomId, SongStatus.PLAYING);
    }

    @Override
    public Optional<Song> getPlayingSong(List<Song> songList) {
        return songList.stream().filter(song -> song.getSongStatus().equals(SongStatus.PLAYING.getSongStatus())).findFirst();
    }

    @Override
    public Optional<Song> getPausedSong(Long roomId) throws DatabaseReadException {
        return getSongByRoomIdAndStatus(roomId, SongStatus.PAUSED);
    }

    @Override
    public Optional<Song> getPausedSong(List<Song> songList) {
        return songList.stream().filter(song -> song.getSongStatus().equals(SongStatus.PAUSED.getSongStatus())).findFirst();
    }

    @Override
    public Optional<Song> getNextSong(Long roomId) throws DatabaseReadException {
        return getSongByRoomIdAndStatus(roomId, SongStatus.NEXT);
    }

    @Override
    public Optional<Song> getNextSong(List<Song> songList) {
        return songList.stream().filter(song -> song.getSongStatus().equals(SongStatus.NEXT.getSongStatus())).findFirst();
    }

    @Override
    public Optional<Song> getPreviousSong(Long roomId) throws DatabaseReadException {
        return songRepo.findFirstByRoomIdAndSongStatusOrderByPlayingTimeDesc(roomId, SongStatus.PLAYED.getSongStatus());
    }

    @Override
    public Optional<Song> getPlayingOrPausedSong(Long roomId) {
        List<Song> songList = getSongListByRoomId(roomId, false);

        Optional<Song> playing = getPlayingSong(songList);
        Optional<Song> paused = getPausedSong(songList);

        if (playing.isPresent()) return playing;
        else return paused;
    }

    @Override
    public Optional<Song> getRecentSong(Long roomId) {
        List<Song> songList = getSongListByRoomId(roomId, true);

        Optional<Song> playing = getPlayingSong(songList);
        Optional<Song> paused = getPausedSong(songList);
        Optional<Song> next = getNextSong(songList);
        Optional<Song> previous = getPreviousSong(roomId);

        if (playing.isPresent()) return playing;
        else if (paused.isPresent()) return paused;
        else if (next.isPresent()) return next;
        else return previous;
    }

    public List<Song> addSongWithSearchModel(Long roomId, SearchModel searchModel) {
        if (searchModel.getType() == SearchType.SONG) {
            return addSongListToRoom(roomId, Collections.singletonList(new Song(roomId, searchModel)));
        } else if (searchModel.getType() == SearchType.ALBUM) {
            return addSongListToRoom(roomId,
                    spotifySearchService.getByAlbumId(searchModel.getId()).stream().map(sm -> new Song(roomId, sm)).collect(Collectors.toList()));
        } else if (searchModel.getType() == SearchType.PLAYLIST) {
            return addSongListToRoom(roomId,
                    spotifySearchService.getByPlaylistId(searchModel.getId()).stream().map(sm -> new Song(roomId, sm)).collect(Collectors.toList()));
        }

        return new LinkedList<>();
    }

    private List<Song> addSongListToRoom(Long roomId, List<Song> songList) {
        List<Song> currentList = getSongListByRoomId(roomId, false);
        int remainingSongCount = Constants.ROOM_SONG_LIMIT - currentList.size();

        List<Song> queuedList = new LinkedList<>();

        Song created = null;
        for (Song s : songList) {
            if (remainingSongCount > 0) {
                try {
                    created = create(s);
                } catch (Exception e) {
                    LOGGER.warn("Song :: Create error from SearchModel");
                }

                if (created != null) {
                    queuedList.add(created);
                }
                remainingSongCount--;
            } else {
                throw new ConstraintViolationException("Max song limit is " + Constants.ROOM_SONG_LIMIT);
            }
        }

        String userName = SecurityHelper.getUserDisplayName();
        String infoMessage = userName + " queued " + queuedList.size() + " songs.\n" + getQueuedSongNames(queuedList);
        webSocketController.sendInfoToRoom(roomId, infoMessage);

        return queuedList;
    }

    @Override
    public boolean removeSongFromRoom(Long songId) {
        String userId = SecurityHelper.getUserId();
        Optional<Song> songOpt = getById(songId);
        Optional<RoomUser> roomUserOpt = roomUserService.getRoomUserByUserId(userId);

        if (!songOpt.isPresent()) {
            String err = String.format("Song[%s] not found", songId);
            throw new NoSuchElementException(err);
        }

        if (!roomUserOpt.isPresent()) {
            String err = String.format("User[%s] not in any room", userId);
            throw new NoSuchElementException(err);
        }

        Long roomId = roomUserOpt.get().getRoomId();

        getPlayingSong(roomId).ifPresent(song -> {
            if (song.getId().equals(songId)) {
                spotifyPlayerService.roomStop(roomId);
            }
        });

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

        songList.forEach(this::delete);

        try {
            String infoMessage = SecurityHelper.getUserDisplayName() + " cleared room queue.";
            webSocketController.sendInfoToRoom(roomId, infoMessage);
        } catch (Exception ignored) {
            webSocketController.sendToRoom("status", roomId, new RoomStatusModel(RoomStatus.CLOSED, "SYSTEM :: Room closed due inactivity."));
        }

        return spotifyPlayerService.roomStop(roomId);
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
    public void updateSongStatus(Song song, SongStatus songStatus) {
        if (songStatus.getSongStatus().equals(SongStatus.PLAYING.getSongStatus())) {
            song.setPlayingTime(TimeHelper.getLocalDateTimeNow());
        } else if (songStatus.getSongStatus().equals(SongStatus.PAUSED.getSongStatus())) {
            song.setCurrentMs(getCumulativePassedMs(song));
        } else if (songStatus.getSongStatus().equals(SongStatus.PLAYED.getSongStatus())) {
            song.setCurrentMs(0);
        }

        song.setSongStatus(songStatus.getSongStatus());
        update(song);
    }

    @Override
    public List<Song> getActiveSongs() {
        try {
            return songRepo.findBySongStatus(SongStatus.PLAYING.getSongStatus());
        } catch (Exception exception) {
            throw new DatabaseReadException(getClassOfEntity(), exception);
        }
    }

    @Override
    public Song getRoomCurrentSong(Long roomId) {
        List<Song> songList = getSongListByRoomId(roomId, true);

        if (songList.isEmpty()) {
            return null;
        } else {
            Song playing = null;
            Song paused = null;
            Song next = null;

            for (Song s : songList) {
                if (s.getSongStatus().equals(SongStatus.PLAYING.getSongStatus())) {
                    playing = s;
                } else if (s.getSongStatus().equals(SongStatus.PAUSED.getSongStatus())) {
                    paused = s;
                } else if (s.getSongStatus().equals(SongStatus.NEXT.getSongStatus())) {
                    if (next == null) {
                        next = s;
                    }
                }
            }

            if (playing != null) {
                return playing;
            } else if (paused != null) {
                return paused;
            } else return next;
        }
    }

    private int updateVote(Long songId, boolean upvote) {
        String userId = SecurityHelper.getUserId();

        Optional<Song> songOpt = getById(songId);
        if (songOpt.isPresent()) {
            Song song = songOpt.get();

            List<String> voters = song.getVoters();
            if (voters.contains(userId)) {
                throw new ConstraintViolationException(song.getSongName() + " :: Voted before");
            } else {
                int votes = song.getVotes();
                String operation;

                votes = (upvote) ? votes + 1 : votes - 1;
                operation = (upvote) ? " upvoted " : " downvoted ";

                voters.add(userId);
                song.setVoters(voters);
                song.setVotes(votes);
                update(song);

                String userName = SecurityHelper.getUserDisplayName();
                String infoMessage = userName + operation + "'" + song.toString() + "'";
                webSocketController.sendInfoToRoom(song.getRoomId(), infoMessage);

                return votes;
            }
        } else {
            String msg = String.format("Song[%s] :: Not found", songId);
            throw new NoSuchElementException(msg);
        }
    }

    private String getQueuedSongNames(List<Song> songs) {
        StringBuilder stringBuilder = new StringBuilder();

        for (Song s : songs) {
            stringBuilder.append("\n").append(s);
        }

        return stringBuilder.toString();
    }

}
