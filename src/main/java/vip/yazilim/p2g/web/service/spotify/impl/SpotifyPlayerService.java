package vip.yazilim.p2g.web.service.spotify.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    @Autowired
    private ISpotifyTokenService tokenService;

    @Autowired
    private ISpotifyRequestService spotifyRequest;

    @Autowired
    private IUserDeviceService userDeviceService;

    @Autowired
    private ISongService songService;

    @Autowired
    private IRoomService roomService;

    @Autowired
    private ISpotifyTokenService spotifyTokenService;

    private Gson gson = new GsonBuilder().create();

    ///////////////////////
    // Room Based
    ///////////////////////
    @Override
    public boolean roomPlay(Song song, int ms, boolean skipCurrentSong) {
        Long roomId = song.getRoomId();

        // JsonArray with song, because uris needs JsonArray as input
        JsonArray urisJson = gson.toJsonTree(Collections.singletonList("spotify:track:" + song.getSongId())).getAsJsonArray();

        // Start playback
        spotifyRequest.execRequestListAsync((spotifyApi, device) -> spotifyApi.startResumeUsersPlayback().uris(urisJson).position_ms(ms).device_id(device).build(), spotifyTokenService.getRoomTokenDeviceMap(roomId));

        if (skipCurrentSong) {
            songService.getPlayingSong(roomId).ifPresent(s -> songService.updateSongStatus(s, SongStatus.PLAYED));
            songService.getPausedSong(roomId).ifPresent(s -> songService.updateSongStatus(s, SongStatus.PLAYED));
        }

        // Update playing
        songService.updateSongStatus(song, SongStatus.PLAYING);

        return true;
    }

    @Override
    public boolean roomPlayPause(Long roomId) {
        Optional<Song> playingOpt = songService.getPlayingSong(roomId);
        Optional<Song> pausedOpt = songService.getPausedSong(roomId);

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
            Optional<Song> firstQueued = songService.getNextSong(roomId);
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
    public boolean roomPrevious(Long roomId) {
        Optional<Song> previousOpt = songService.getPreviousSong(roomId);

        if (previousOpt.isPresent()) {
            songService.getPlayingSong(roomId).ifPresent(next -> songService.updateSongStatus(next, SongStatus.NEXT));
            songService.getPausedSong(roomId).ifPresent(played -> songService.updateSongStatus(played, SongStatus.PLAYED));
            return roomPlay(previousOpt.get(), 0, false);
        } else {
            throw new NoSuchElementException("Previous song is empty");
        }
    }

    @Override
    public boolean roomSeek(Long roomId, Integer ms) {
        Optional<Song> playingOpt = songService.getPlayingSong(roomId);
        Optional<Song> pausedOpt = songService.getPausedSong(roomId);
        Optional<Song> nextOpt = songService.getNextSong(roomId);

        if (playingOpt.isPresent()) {
            Song playing = playingOpt.get();
            playing.setCurrentMs(ms);
            playing.setPlayingTime(TimeHelper.getLocalDateTimeNow());
            songService.update(playing);

            spotifyRequest.execRequestListAsync((spotifyApi, device) -> spotifyApi.seekToPositionInCurrentlyPlayingTrack(ms).device_id(device).build(), spotifyTokenService.getRoomTokenDeviceMap(roomId));
        } else if (pausedOpt.isPresent()) {
            Song paused = pausedOpt.get();
            paused.setCurrentMs(ms);
            songService.update(paused);

            spotifyRequest.execRequestListAsync((spotifyApi, device) -> spotifyApi.seekToPositionInCurrentlyPlayingTrack(ms).device_id(device).build(), spotifyTokenService.getRoomTokenDeviceMap(roomId));
        } else if (nextOpt.isPresent()) {
            Song next = nextOpt.get();
            next.setCurrentMs(ms);
            next.setPlayingTime(TimeHelper.getLocalDateTimeNow());
            songService.updateSongStatus(next, SongStatus.PAUSED);
            songService.update(next);

            spotifyRequest.execRequestListAsync((spotifyApi, device) -> spotifyApi.seekToPositionInCurrentlyPlayingTrack(ms).device_id(device).build(), spotifyTokenService.getRoomTokenDeviceMap(roomId));
        } else {
            throw new NoSuchElementException("No playing or paused song found in the room");
        }

        return true;
    }

    @Override
    public boolean roomRepeat(Long roomId) {
        Optional<Song> playingOpt = songService.getPlayingSong(roomId);
        Optional<Song> pausedOpt = songService.getPausedSong(roomId);

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
        Boolean repeatFlag = song.getRepeatFlag();

        if (repeatFlag == null || !repeatFlag) {
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

        roomOpt.ifPresent(room -> {
            Optional<Song> playingOpt = songService.getPlayingSong(room.getId());
            Optional<Song> pausedOpt = songService.getPausedSong(room.getId());

            playingOpt.map(song -> playUser(userId, song)).orElseGet(() -> pausedOpt.filter(song -> sync(userId, song)).isPresent());
        });

        return true;
    }

    @Override
    public void desyncWithRoom(String userId) {
        Optional<OAuthToken> token = tokenService.getTokenByUserId(userId);
        Optional<UserDevice> userDeviceOpt = userDeviceService.getUsersActiveDevice(userId);

        if (token.isPresent() && userDeviceOpt.isPresent()) {
            String accessToken = token.get().getAccessToken();
            String deviceId = userDeviceOpt.get().getId();

            spotifyRequest.execRequestAsync((spotifyApi) -> spotifyApi.pauseUsersPlayback().device_id(deviceId).build(), accessToken);
        }
    }

    private boolean playUser(String userId, Song song) {
        Optional<OAuthToken> token = tokenService.getTokenByUserId(userId);
        Optional<UserDevice> userDeviceOpt = userDeviceService.getUsersActiveDevice(userId);

        if (token.isPresent() && userDeviceOpt.isPresent()) {
            String accessToken = token.get().getAccessToken();
            String deviceId = userDeviceOpt.get().getId();

            List<String> songList = Collections.singletonList("spotify:track:" + song.getSongId());
            JsonArray urisJson = new GsonBuilder().create().toJsonTree(songList).getAsJsonArray();

            spotifyRequest.execRequestAsync((spotifyApi) -> spotifyApi.startResumeUsersPlayback().uris(urisJson).position_ms(getCumulativePassedMs(song)).device_id(deviceId).build(), accessToken);
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
            spotifyRequest.execRequestAsync((spotifyApi) -> spotifyApi.seekToPositionInCurrentlyPlayingTrack(ms).device_id(deviceId).build(), accessToken);
            return true;
        }
        return false;
    }

}
