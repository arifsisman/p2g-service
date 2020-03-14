package vip.yazilim.p2g.web.service.spotify.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vip.yazilim.libs.springcore.exception.general.InvalidArgumentException;
import vip.yazilim.libs.springcore.exception.general.InvalidUpdateException;
import vip.yazilim.libs.springcore.exception.general.database.DatabaseException;
import vip.yazilim.libs.springcore.exception.service.ResourceNotFoundException;
import vip.yazilim.p2g.web.constant.enums.SearchType;
import vip.yazilim.p2g.web.constant.enums.SongStatus;
import vip.yazilim.p2g.web.entity.*;
import vip.yazilim.p2g.web.service.p2g.ISongService;
import vip.yazilim.p2g.web.service.p2g.ISpotifyTokenService;
import vip.yazilim.p2g.web.service.p2g.IUserDeviceService;
import vip.yazilim.p2g.web.service.p2g.IUserService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyPlayerService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyRequestService;
import vip.yazilim.p2g.web.service.spotify.model.PlayerModel;
import vip.yazilim.p2g.web.util.TimeHelper;

import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class SpotifyPlayerService implements ISpotifyPlayerService {

    @Autowired
    private ISpotifyTokenService tokenService;

    @Autowired
    private ISpotifyRequestService spotifyRequest;

    @Autowired
    private IUserService userService;

    @Autowired
    private IUserDeviceService userDeviceService;

    @Autowired
    private ISongService songService;

    private Gson gson = new GsonBuilder().create();

    ///////////////////////
    // Room Based
    ///////////////////////
    @Override
    public boolean roomPlay(Song song, int ms, boolean skipCurrentSong) throws DatabaseException, InvalidArgumentException, IOException, SpotifyWebApiException, InvalidUpdateException {
        Long roomId = song.getRoomId();

        if (skipCurrentSong) {
            Optional<Song> playingOpt = songService.getPlayingSong(roomId);
            Optional<Song> pausedOpt = songService.getPausedSong(roomId);

            if (playingOpt.isPresent()) {
                Song playing = playingOpt.get();

                playing.setCurrentMs(0);
                playing.setSongStatus(SongStatus.PLAYED.getSongStatus());
                songService.update(playing);
            } else if (pausedOpt.isPresent()) {
                Song paused = pausedOpt.get();

                paused.setCurrentMs(0);
                paused.setSongStatus(SongStatus.PLAYED.getSongStatus());
                songService.update(paused);
            }
        }

        // JsonArray with song, because uris needs JsonArray as input
        JsonArray urisJson = gson.toJsonTree(Collections.singletonList("spotify:track:" + song.getSongId())).getAsJsonArray();

        // Update playing
        song.setPlayingTime(TimeHelper.getLocalDateTimeNow());
        song.setSongStatus(SongStatus.PLAYING.getSongStatus());
        songService.update(song);

        // Start playback
        spotifyRequest.execRequestListAsync((spotifyApi, device) -> spotifyApi.startResumeUsersPlayback().uris(urisJson).position_ms(ms).device_id(device).build(), getRoomTokenDeviceMap(roomId));

        return true;
    }

    @Override
    public boolean roomPlayPause(Long roomId) throws DatabaseException, InvalidArgumentException, IOException, SpotifyWebApiException, InvalidUpdateException {
        Optional<Song> playingOpt = songService.getPlayingSong(roomId);
        Optional<Song> pausedOpt = songService.getPausedSong(roomId);

        if (playingOpt.isPresent()) {
            Song playing = playingOpt.get();

            // Pause playback
            spotifyRequest.execRequestListAsync((spotifyApi, device) -> spotifyApi.pauseUsersPlayback().device_id(device).build(), getRoomTokenDeviceMap(roomId));

            // Update playing
            playing.setCurrentMs(getCumulativePassedMs(playing));
            playing.setSongStatus(SongStatus.PAUSED.getSongStatus());
            songService.update(playing);
        } else if (pausedOpt.isPresent()) {
            Song paused = pausedOpt.get();
            int currentMs = Math.toIntExact(paused.getCurrentMs());

            // Resume playback
            roomPlay(paused, currentMs, false);

            // Update paused
            paused.setPlayingTime(TimeHelper.getLocalDateTimeNow());
            paused.setSongStatus(SongStatus.PLAYING.getSongStatus());
            songService.update(paused);
        } else {
            Optional<Song> firstQueued = songService.getNextSong(roomId);
            roomPlay(firstQueued.orElseThrow(() -> new ResourceNotFoundException("Queue is empty")), 0, false);
        }

        return true;
    }

    @Override
    public boolean roomNext(Long roomId) throws DatabaseException, InvalidArgumentException, IOException, SpotifyWebApiException, InvalidUpdateException {
        Optional<Song> next = songService.getNextSong(roomId);

        if (next.isPresent()) {
            return roomPlay(next.get(), 0, true);
        } else {
            throw new ResourceNotFoundException("Next song is empty");
        }
    }

    @Override
    public boolean roomPrevious(Long roomId) throws DatabaseException, InvalidArgumentException, IOException, SpotifyWebApiException, InvalidUpdateException {
        Optional<Song> previousOpt = songService.getPreviousSong(roomId);

        if (previousOpt.isPresent()) {
            Optional<Song> playingOpt = songService.getPlayingSong(roomId);
            if (playingOpt.isPresent()) { // Set old playing as next, if present
                Song playing = playingOpt.get();
                playing.setSongStatus(SongStatus.NEXT.getSongStatus());
                songService.update(playing);
            }

            Optional<Song> pausedOpt = songService.getPausedSong(roomId);
            if (pausedOpt.isPresent()) { // Set old paused as played, if present
                Song paused = pausedOpt.get();
                paused.setSongStatus(SongStatus.PLAYED.getSongStatus());
                songService.update(paused);
            }

            return roomPlay(previousOpt.get(), 0, false);
        } else {
            throw new ResourceNotFoundException("Previous song is empty");
        }
    }

    @Override
    public boolean roomSeek(Long roomId, Integer ms) throws DatabaseException, InvalidArgumentException, IOException, SpotifyWebApiException, InvalidUpdateException {
        Optional<Song> playingOpt = songService.getPlayingSong(roomId);
        Optional<Song> pausedOpt = songService.getPausedSong(roomId);

        if (playingOpt.isPresent()) {
            Song playing = playingOpt.get();
            playing.setCurrentMs(ms);
            playing.setPlayingTime(TimeHelper.getLocalDateTimeNow());
            songService.update(playing);

            spotifyRequest.execRequestListAsync((spotifyApi, device) -> spotifyApi.seekToPositionInCurrentlyPlayingTrack(ms).device_id(device).build(), getRoomTokenDeviceMap(roomId));
        } else if (pausedOpt.isPresent()) {
            Song paused = pausedOpt.get();
            paused.setCurrentMs(ms);
            songService.update(paused);

            spotifyRequest.execRequestListAsync((spotifyApi, device) -> spotifyApi.seekToPositionInCurrentlyPlayingTrack(ms).device_id(device).build(), getRoomTokenDeviceMap(roomId));
        } else {
            String err = String.format("Not playing or paused any song in Room[%s]", roomId);
            throw new ResourceNotFoundException(err);
        }

        return true;
    }

    @Override
    public boolean roomRepeat(Long roomId) throws DatabaseException, InvalidArgumentException, IOException, SpotifyWebApiException, InvalidUpdateException {
        Optional<Song> playingOpt = songService.getPlayingSong(roomId);
        Optional<Song> pausedOpt = songService.getPausedSong(roomId);

        if (playingOpt.isPresent()) {
            Song playing = playingOpt.get();

            playing.setCurrentMs(getCumulativePassedMs(playing));
            playing.setSongStatus(SongStatus.PLAYING.getSongStatus());
            songService.update(playing);

            return roomRepeatHelper(roomId, playing);
        } else if (pausedOpt.isPresent()) {
            return roomRepeatHelper(roomId, pausedOpt.get());
        } else {
            throw new ResourceNotFoundException("Song not found for repeat");
        }
    }

    private int getCumulativePassedMs(Song song) {
        return (int) (ChronoUnit.MILLIS.between(song.getPlayingTime(), TimeHelper.getLocalDateTimeNow()) + song.getCurrentMs());
    }

    private boolean roomRepeatHelper(Long roomId, Song song) throws DatabaseException, InvalidArgumentException, IOException, SpotifyWebApiException, InvalidUpdateException {
        String repeatMode;
        Boolean repeatFlag = song.getRepeatFlag();

        if (repeatFlag == null || !repeatFlag) {
            repeatMode = SearchType.SONG.getType();
            repeatFlag = true;
        } else {
            repeatMode = "off";
            repeatFlag = false;
        }

        spotifyRequest.execRequestListAsync((spotifyApi, device) -> spotifyApi.setRepeatModeOnUsersPlayback(repeatMode).device_id(device).build(), getRoomTokenDeviceMap(roomId));

        song.setRepeatFlag(repeatFlag);
        songService.update(song);

        return repeatFlag;
    }

    ///////////////////////
    // User Based
    ///////////////////////

    @Override
    public boolean userSyncWithRoom(RoomUser roomUser) throws DatabaseException, IOException, SpotifyWebApiException {
        Optional<Song> playingOpt = songService.getPlayingSong(roomUser.getRoomId());
        Optional<Song> pausedOpt = songService.getPausedSong(roomUser.getRoomId());

        if (playingOpt.isPresent()) { // play currently playing song with current ms
            return play(roomUser, playingOpt.get());
        } else if (pausedOpt.isPresent()) { // seek to current ms
            return sync(roomUser, pausedOpt.get());
        } else { // noop
            return false;
        }
    }

    @Override
    public boolean roomStop(Long roomId) throws DatabaseException, InvalidArgumentException, IOException, SpotifyWebApiException {
        spotifyRequest.execRequestListAsync((spotifyApi, device) -> spotifyApi.pauseUsersPlayback().device_id(device).build(), getRoomTokenDeviceMap(roomId));
        return true;
    }

    private boolean play(RoomUser roomUser, Song song) throws DatabaseException, IOException, SpotifyWebApiException {
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

    private boolean sync(RoomUser roomUser, Song song) throws DatabaseException, IOException, SpotifyWebApiException {
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
    public boolean userDeSyncWithRoom(RoomUser roomUser) throws DatabaseException, IOException, SpotifyWebApiException {
        String userId = roomUser.getUserId();

        Optional<OAuthToken> token = tokenService.getTokenByUserId(userId);
        Optional<UserDevice> userDeviceOpt = userDeviceService.getUsersActiveDevice(userId);

        if (token.isPresent() && userDeviceOpt.isPresent()) {
            String accessToken = token.get().getAccessToken();
            String deviceId = userDeviceOpt.get().getId();

            spotifyRequest.execRequestAsync((spotifyApi) -> spotifyApi.pauseUsersPlayback().device_id(deviceId).build(), accessToken);
            return true;
        }
        return false;
    }

    ///////////////////////
    // Model getter methods
    ///////////////////////

    private PlayerModel getRoomPlayerModel(Long roomId) throws DatabaseException, InvalidArgumentException {
        List<String> spotifyTokenList = new LinkedList<>();
        List<String> userDeviceList = new LinkedList<>();

        List<User> userList = userService.getUsersByRoomId(roomId);

        for (User u : userList) {
            String userId = u.getId();
            Optional<OAuthToken> token = tokenService.getTokenByUserId(userId);

            token.ifPresent(spotifyToken -> spotifyTokenList.add(spotifyToken.getAccessToken()));

            Optional<UserDevice> userDeviceOpt = userDeviceService.getUsersActiveDevice(userId);
            userDeviceOpt.ifPresent(userDevice -> userDeviceList.add(userDevice.getId()));
        }

        PlayerModel playerModel = new PlayerModel();
        playerModel.setRoomId(roomId);
        playerModel.setSpotifyTokenList(spotifyTokenList);
        playerModel.setUserDeviceList(userDeviceList);

        return playerModel;
    }

    private Map<String, String> getRoomTokenDeviceMap(Long roomId) throws DatabaseException, InvalidArgumentException {
        Map<String, String> map = new HashMap<>();

        List<User> userList = userService.getUsersByRoomId(roomId);

        for (User u : userList) {
            String userId = u.getId();
            Optional<OAuthToken> token = tokenService.getTokenByUserId(userId);
            Optional<UserDevice> userDeviceOpt = userDeviceService.getUsersActiveDevice(userId);

            if (token.isPresent() && userDeviceOpt.isPresent()) {
                map.put(token.get().getAccessToken(), userDeviceOpt.get().getId());
            }
        }

        return map;
    }

    private HashMap<String, String> getUserTokenDeviceMap(String userId) throws DatabaseException {
        HashMap<String, String> map = new HashMap<>();

        Optional<OAuthToken> token = tokenService.getTokenByUserId(userId);
        Optional<UserDevice> userDeviceOpt = userDeviceService.getUsersActiveDevice(userId);

        if (token.isPresent() && userDeviceOpt.isPresent()) {
            map.put(token.get().getAccessToken(), userDeviceOpt.get().getId());
        }

        return map;
    }

}
