package vip.yazilim.p2g.web.service.spotify.impl;

import com.google.gson.JsonArray;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.config.GsonConfig;
import vip.yazilim.p2g.web.entity.OAuthToken;
import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.p2g.web.entity.UserDevice;
import vip.yazilim.p2g.web.enums.SearchType;
import vip.yazilim.p2g.web.enums.SongStatus;
import vip.yazilim.p2g.web.service.p2g.IRoomService;
import vip.yazilim.p2g.web.service.p2g.ISongService;
import vip.yazilim.p2g.web.service.p2g.ISpotifyTokenService;
import vip.yazilim.p2g.web.service.p2g.IUserDeviceService;
import vip.yazilim.p2g.web.service.spotify.IPlayerService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyRequestService;
import vip.yazilim.p2g.web.util.TimeHelper;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static vip.yazilim.p2g.web.service.p2g.impl.SongService.getCumulativePassedMs;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class SpotifyPlayerService implements IPlayerService {

    private final ISpotifyTokenService tokenService;
    private final ISpotifyRequestService spotifyRequest;
    private final IUserDeviceService userDeviceService;
    private final ISongService songService;
    private final IRoomService roomService;
    private final ISpotifyTokenService spotifyTokenService;
    private final GsonConfig gsonConfig;

    public SpotifyPlayerService(ISpotifyTokenService tokenService,
                                ISpotifyRequestService spotifyRequest,
                                IUserDeviceService userDeviceService,
                                ISongService songService,
                                @Lazy IRoomService roomService,
                                ISpotifyTokenService spotifyTokenService,
                                GsonConfig gsonConfig) {
        this.tokenService = tokenService;
        this.spotifyRequest = spotifyRequest;
        this.userDeviceService = userDeviceService;
        this.songService = songService;
        this.roomService = roomService;
        this.spotifyTokenService = spotifyTokenService;
        this.gsonConfig = gsonConfig;
    }

    ///////////////////////
    // Room Based
    ///////////////////////
    @Override
    public boolean roomPlay(Song song, int ms, boolean skipCurrentSong) {
        Long roomId = song.getRoomId();

        // JsonArray with song, because uris needs JsonArray as input
        JsonArray urisJson = gsonConfig.getGson().toJsonTree(Collections.singletonList("spotify:track:" + song.getSongId())).getAsJsonArray();

        // Start playback
        spotifyRequest.execRequestListAsync((spotifyApi, device) -> spotifyApi.startResumeUsersPlayback().uris(urisJson).position_ms(ms).device_id(device).build(), spotifyTokenService.getRoomTokenDeviceMap(roomId));

        if (skipCurrentSong) {
            List<Song> songList = songService.getSongListByRoomId(roomId, false);

            songService.getPlayingSong(songList).ifPresent(playing -> songService.updateSongStatus(playing, SongStatus.PLAYED));
            songService.getPausedSong(songList).ifPresent(paused -> songService.updateSongStatus(paused, SongStatus.PLAYED));
        }

        // Update playing
        songService.updateSongStatus(song, SongStatus.PLAYING);

        return true;
    }

    @Override
    public boolean roomPlayPause(Long roomId) {
        List<Song> songList = songService.getSongListByRoomId(roomId, false);

        Optional<Song> playingOpt = songService.getPlayingSong(songList);
        Optional<Song> pausedOpt = songService.getPausedSong(songList);

        if (playingOpt.isPresent()) {
            // Pause playback
            spotifyRequest.execRequestListAsync((spotifyApi, device) -> spotifyApi.pauseUsersPlayback().device_id(device).build(), spotifyTokenService.getRoomTokenDeviceMap(roomId));

            // Update playing
            playingOpt.ifPresent(song -> songService.updateSongStatus(song, SongStatus.PAUSED));
        } else if (pausedOpt.isPresent()) {
            Song paused = pausedOpt.get();
            int currentMs = Math.toIntExact(paused.getCurrentMs());

            // Resume playback
            roomPlay(paused, currentMs, false);

            // Update paused
            songService.updateSongStatus(paused, SongStatus.PLAYING);
        } else {
            Optional<Song> firstQueued = songService.getNextSong(songList);
            roomPlay(firstQueued.orElseThrow(() -> new NoSuchElementException("Queue is empty")), 0, false);
        }

        return true;
    }

    @Override
    public boolean roomNext(Long roomId) {
        Optional<Song> nextOpt = songService.getNextSong(roomId);

        if (nextOpt.isPresent()) {
            return roomPlay(nextOpt.get(), 0, true);
        } else {
            throw new NoSuchElementException("Next song is empty");
        }
    }

    @Override
    public boolean roomNext(Song song) {
        return roomPlay(song, 0, true);
    }

    @Override
    public boolean roomPrevious(Long roomId) {
        Optional<Song> previousOpt = songService.getPreviousSong(roomId);

        if (previousOpt.isPresent()) {
            List<Song> songList = songService.getSongListByRoomId(roomId, false);

            songService.getPlayingSong(songList).ifPresent(next -> songService.updateSongStatus(next, SongStatus.NEXT));
            songService.getPausedSong(songList).ifPresent(played -> songService.updateSongStatus(played, SongStatus.PLAYED));

            return roomPlay(previousOpt.get(), 0, false);
        } else {
            throw new NoSuchElementException("Previous song is empty");
        }
    }

    @Override
    public boolean roomSeek(Long roomId, int ms) {
        Optional<Song> recentOpt = songService.getRecentSong(roomId, false);

        if (recentOpt.isPresent()) {
            Song recent = recentOpt.get();

            if (SongStatus.valueOf(recent.getSongStatus()).equals(SongStatus.PLAYING)) {
                recent.setPlayingTime(TimeHelper.getLocalDateTimeNow());
            } else if (SongStatus.valueOf(recent.getSongStatus()).equals(SongStatus.NEXT)) {
                recent.setPlayingTime(TimeHelper.getLocalDateTimeNow());
                songService.updateSongStatus(recent, SongStatus.PAUSED);
            }

            recent.setCurrentMs(ms);
            songService.update(recent);
            spotifyRequest.execRequestListAsync((spotifyApi, device) -> spotifyApi.seekToPositionInCurrentlyPlayingTrack(ms).device_id(device).build(), spotifyTokenService.getRoomTokenDeviceMap(roomId));

            return true;
        } else {
            throw new NoSuchElementException("No songs found in the room");
        }
    }

    @Override
    public boolean roomRepeat(Long roomId) {
        List<Song> songList = songService.getSongListByRoomId(roomId, false);

        Optional<Song> playingOpt = songService.getPlayingSong(songList);
        Optional<Song> pausedOpt = songService.getPausedSong(songList);

        if (playingOpt.isPresent()) {
            Song playing = playingOpt.get();

            playing.setCurrentMs(getCumulativePassedMs(playing));
            songService.updateSongStatus(playing, SongStatus.PLAYING);

            return roomRepeatHelper(roomId, playing);
        } else if (pausedOpt.isPresent()) {
            return roomRepeatHelper(roomId, pausedOpt.get());
        } else {
            throw new NoSuchElementException("Song not found for repeat");
        }
    }

    private boolean roomRepeatHelper(Long roomId, Song song) {
        String repeatMode;
        boolean repeatFlag = song.isRepeatFlag();

        if (!repeatFlag) {
            repeatMode = SearchType.SONG.getType();
            repeatFlag = true;
        } else {
            repeatMode = "off";
            repeatFlag = false;
        }

        spotifyRequest.execRequestListAsync((spotifyApi, device) -> spotifyApi.setRepeatModeOnUsersPlayback(repeatMode).device_id(device).build(), spotifyTokenService.getRoomTokenDeviceMap(roomId));

        song.setRepeatFlag(repeatFlag);
        songService.update(song);

        return repeatFlag;
    }

    @Override
    public boolean roomStop(Long roomId) {
        spotifyRequest.execRequestListAsync((spotifyApi, device) -> spotifyApi.pauseUsersPlayback().device_id(device).build(), spotifyTokenService.getRoomTokenDeviceMap(roomId));
        return true;
    }

    @Override
    public boolean syncWithRoom(String userId) {
        Optional<Room> roomOpt = roomService.getRoomByUserId(userId);

        if (roomOpt.isPresent()) {
            Room room = roomOpt.get();

            Long roomId = room.getId();
            List<Song> songList = songService.getSongListByRoomId(roomId, false);

            Optional<Song> playingOpt = songService.getPlayingSong(songList);
            Optional<Song> pausedOpt = songService.getPausedSong(songList);

            return playingOpt.map(song -> playUser(userId, song)).orElseGet(() -> pausedOpt.filter(song -> sync(userId, song)).isPresent());
        } else {
            throw new NoSuchElementException("Room :: Not found");
        }
    }

    @Override
    public void desyncWithRoom(String userId) {
        Optional<OAuthToken> token = tokenService.getTokenByUserId(userId);
        Optional<UserDevice> userDeviceOpt = userDeviceService.getUsersActiveDevice(userId);

        if (token.isPresent() && userDeviceOpt.isPresent()) {
            String accessToken = token.get().getAccessToken();
            String deviceId = userDeviceOpt.get().getId();

            spotifyRequest.execRequestAsync(spotifyApi -> spotifyApi.pauseUsersPlayback().device_id(deviceId).build(), accessToken);
        }
    }

    private boolean playUser(String userId, Song song) {
        Optional<OAuthToken> token = tokenService.getTokenByUserId(userId);
        Optional<UserDevice> userDeviceOpt = userDeviceService.getUsersActiveDevice(userId);

        if (token.isPresent() && userDeviceOpt.isPresent()) {
            String accessToken = token.get().getAccessToken();
            String deviceId = userDeviceOpt.get().getId();

            List<String> songList = Collections.singletonList("spotify:track:" + song.getSongId());
            JsonArray urisJson = gsonConfig.getGson().toJsonTree(songList).getAsJsonArray();

            spotifyRequest.execRequestAsync(spotifyApi -> spotifyApi.startResumeUsersPlayback().uris(urisJson).position_ms(getCumulativePassedMs(song)).device_id(deviceId).build(), accessToken);
            return true;
        }

        return false;
    }

    private boolean sync(String userId, Song song) {
        Optional<OAuthToken> token = tokenService.getTokenByUserId(userId);
        Optional<UserDevice> userDeviceOpt = userDeviceService.getUsersActiveDevice(userId);

        if (token.isPresent() && userDeviceOpt.isPresent()) {
            String accessToken = token.get().getAccessToken();
            String deviceId = userDeviceOpt.get().getId();

            int ms = Math.toIntExact(song.getCurrentMs());
            spotifyRequest.execRequestAsync(spotifyApi -> spotifyApi.seekToPositionInCurrentlyPlayingTrack(ms).device_id(deviceId).build(), accessToken);
            return true;
        }
        return false;
    }

}
