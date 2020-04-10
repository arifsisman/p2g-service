package vip.yazilim.p2g.web.service.spotify.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.OAuthToken;
import vip.yazilim.p2g.web.entity.RoomUser;
import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.p2g.web.entity.UserDevice;
import vip.yazilim.p2g.web.enums.SearchType;
import vip.yazilim.p2g.web.enums.SongStatus;
import vip.yazilim.p2g.web.service.p2g.ISongService;
import vip.yazilim.p2g.web.service.p2g.ISpotifyTokenService;
import vip.yazilim.p2g.web.service.p2g.IUserDeviceService;
import vip.yazilim.p2g.web.service.spotify.IPlayerService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyRequestService;
import vip.yazilim.p2g.web.util.TimeHelper;

import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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
    private ISpotifyTokenService spotifyTokenService;

    private Gson gson = new GsonBuilder().create();

    ///////////////////////
    // Room Based
    ///////////////////////
    @Override
    public boolean roomPlay(Song song, int ms, boolean skipCurrentSong) {
        Long roomId = song.getRoomId();

        if (skipCurrentSong) {
            Optional<Song> playingOpt = songService.getPlayingSong(roomId);
            Optional<Song> pausedOpt = songService.getPausedSong(roomId);

            if (playingOpt.isPresent()) {
                Song playing = playingOpt.get();

                //todo check
                playing.setCurrentMs(0);
                songService.updateSongStatus(playing, SongStatus.PLAYED);
            } else if (pausedOpt.isPresent()) {
                Song paused = pausedOpt.get();

                paused.setCurrentMs(0);
                songService.updateSongStatus(paused, SongStatus.PLAYED);
            }
        }

        // JsonArray with song, because uris needs JsonArray as input
        JsonArray urisJson = gson.toJsonTree(Collections.singletonList("spotify:track:" + song.getSongId())).getAsJsonArray();

        // Update playing
        songService.updateSongStatus(song, SongStatus.PLAYING);

        // Start playback
        spotifyRequest.execRequestListAsync((spotifyApi, device) -> spotifyApi.startResumeUsersPlayback().uris(urisJson).position_ms(ms).device_id(device).build(), spotifyTokenService.getRoomTokenDeviceMap(roomId));

        return true;
    }

    @Override
    public boolean roomPlayPause(Long roomId) {
        Optional<Song> playingOpt = songService.getPlayingSong(roomId);
        Optional<Song> pausedOpt = songService.getPausedSong(roomId);

        if (playingOpt.isPresent()) {
            Song playing = playingOpt.get();

            // Pause playback
            spotifyRequest.execRequestListAsync((spotifyApi, device) -> spotifyApi.pauseUsersPlayback().device_id(device).build(), spotifyTokenService.getRoomTokenDeviceMap(roomId));

            // Update playing
            playing.setCurrentMs(getCumulativePassedMs(playing));
            songService.updateSongStatus(playing, SongStatus.PAUSED);
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
        Optional<Song> next = songService.getNextSong(roomId);

        if (next.isPresent()) {
            return roomPlay(next.get(), 0, true);
        } else {
            throw new NoSuchElementException("Next song is empty");
        }
    }

    @Override
    public boolean roomPrevious(Long roomId) {
        Optional<Song> previousOpt = songService.getPreviousSong(roomId);

        if (previousOpt.isPresent()) {
            // Set old playing as next, if present
            songService.getPlayingSong(roomId).ifPresent(song -> songService.updateSongStatus(song, SongStatus.NEXT));

            // Set old paused as played, if present
            songService.getPausedSong(roomId).ifPresent(song -> songService.updateSongStatus(song, SongStatus.PLAYED));

            return roomPlay(previousOpt.get(), 0, false);
        } else {
            throw new NoSuchElementException("Previous song is empty");
        }
    }

    @Override
    public boolean roomSeek(Long roomId, Integer ms) {
        Optional<Song> playingOpt = songService.getPlayingSong(roomId);
        Optional<Song> pausedOpt = songService.getPausedSong(roomId);

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

            //todo check setCurrentMs
            playing.setCurrentMs(getCumulativePassedMs(playing));
            songService.updateSongStatus(playing, SongStatus.PLAYING);

            return roomRepeatHelper(roomId, playing);
        } else if (pausedOpt.isPresent()) {
            return roomRepeatHelper(roomId, pausedOpt.get());
        } else {
            throw new NoSuchElementException("Song not found for repeat");
        }
    }

    private int getCumulativePassedMs(Song song) {
        return (int) (ChronoUnit.MILLIS.between(song.getPlayingTime(), TimeHelper.getLocalDateTimeNow()) + song.getCurrentMs());
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

    ///////////////////////
    // User Based
    ///////////////////////

    @Override
    public boolean userSyncWithRoom(RoomUser roomUser) {
        Optional<Song> playingOpt = songService.getPlayingSong(roomUser.getRoomId());
        Optional<Song> pausedOpt = songService.getPausedSong(roomUser.getRoomId());

        // seek to current ms
        // noop
        // play currently playing song with current ms
        return playingOpt.map(song -> play(roomUser, song)).orElseGet(() -> pausedOpt.filter(song -> sync(roomUser, song)).isPresent());
    }

    @Override
    public boolean roomStop(Long roomId) {
        spotifyRequest.execRequestListAsync((spotifyApi, device) -> spotifyApi.pauseUsersPlayback().device_id(device).build(), spotifyTokenService.getRoomTokenDeviceMap(roomId));
        return true;
    }

    private boolean play(RoomUser roomUser, Song song) {
        String userId = roomUser.getUserId();

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

    private boolean sync(RoomUser roomUser, Song song) {
        String userId = roomUser.getUserId();
        int ms = Math.toIntExact(song.getCurrentMs());

        Optional<OAuthToken> token = tokenService.getTokenByUserId(userId);
        Optional<UserDevice> userDeviceOpt = userDeviceService.getUsersActiveDevice(userId);

        if (token.isPresent() && userDeviceOpt.isPresent()) {
            String accessToken = token.get().getAccessToken();
            String deviceId = userDeviceOpt.get().getId();

            spotifyRequest.execRequestAsync((spotifyApi) -> spotifyApi.seekToPositionInCurrentlyPlayingTrack(ms).device_id(deviceId).build(), accessToken);
            return true;
        }
        return false;
    }

    @Override
    public void userDeSyncWithRoom(RoomUser roomUser) {
        String userId = roomUser.getUserId();

        Optional<OAuthToken> token = tokenService.getTokenByUserId(userId);
        Optional<UserDevice> userDeviceOpt = userDeviceService.getUsersActiveDevice(userId);

        if (token.isPresent() && userDeviceOpt.isPresent()) {
            String accessToken = token.get().getAccessToken();
            String deviceId = userDeviceOpt.get().getId();

            spotifyRequest.execRequestAsync((spotifyApi) -> spotifyApi.pauseUsersPlayback().device_id(deviceId).build(), accessToken);
        }
    }

}
