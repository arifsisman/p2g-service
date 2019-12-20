package vip.yazilim.p2g.web.service.spotify.impl;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.wrapper.spotify.enums.ModelObjectType;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.constant.SongStatus;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.entity.relation.RoomUser;
import vip.yazilim.p2g.web.entity.relation.Song;
import vip.yazilim.p2g.web.entity.relation.SpotifyToken;
import vip.yazilim.p2g.web.entity.relation.UserDevice;
import vip.yazilim.p2g.web.service.p2g.ITokenService;
import vip.yazilim.p2g.web.service.p2g.IUserService;
import vip.yazilim.p2g.web.service.p2g.relation.ISongService;
import vip.yazilim.p2g.web.service.p2g.relation.IUserDeviceService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyPlayerService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyRequestService;
import vip.yazilim.p2g.web.service.spotify.model.PlayerModel;
import vip.yazilim.p2g.web.util.TimeHelper;
import vip.yazilim.spring.core.exception.general.InvalidArgumentException;
import vip.yazilim.spring.core.exception.general.InvalidUpdateException;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;
import vip.yazilim.spring.core.exception.web.NotFoundException;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Transactional
@Service
public class SpotifyPlayerService implements ISpotifyPlayerService {

    @Autowired
    private ITokenService tokenService;

    @Autowired
    private ISpotifyRequestService spotifyRequest;

    @Autowired
    private IUserService userService;

    @Autowired
    private IUserDeviceService userDeviceService;

    @Autowired
    private ISongService songService;

    ///////////////////////
    // Room Based
    ///////////////////////
    @Override
    public List<Song> roomPlay(Song song, int ms) throws DatabaseException, InvalidUpdateException, InvalidArgumentException, IOException, SpotifyWebApiException {
        String songUri = song.getSongUri();
        String roomUuid = song.getRoomUuid();

        if (!songUri.contains(ModelObjectType.TRACK.getType())) {
            String err = String.format("URI[%s] does not match with an Track URI", songUri);
            throw new InvalidArgumentException(err);
        } else {
            // JsonArray with song, because uris needs JsonArray as input
            List<String> songList = Collections.singletonList(songUri);
            JsonArray urisJson = new GsonBuilder().create().toJsonTree(songList).getAsJsonArray();

            // Start playback
            spotifyRequest.execRequestListAsync((spotifyApi, device) -> spotifyApi.startResumeUsersPlayback().uris(urisJson).position_ms(ms).device_id(device).build(), getRoomTokenDeviceMap(roomUuid));

            // Update playing
            song.setPlayingTime(TimeHelper.getLocalDateTimeNow());
            song.setSongStatus(SongStatus.PLAYING.getSongStatus());
            songService.update(song);
        }

        return songService.getSongListByRoomUuid(roomUuid);
    }

    @Override
    public List<Song> roomStartResume(String roomUuid) throws DatabaseException, InvalidUpdateException, InvalidArgumentException, IOException, SpotifyWebApiException {
        Optional<Song> pausedOpt = songService.getPausedSong(roomUuid);

        if (pausedOpt.isPresent()) {
            Song paused = pausedOpt.get();
            int currentMs = Math.toIntExact(paused.getCurrentMs());

            // Resume playback
            roomPlay(paused, currentMs);

            // Update playing
            paused.setSongStatus(SongStatus.PLAYING.getSongStatus());
            songService.update(paused);
        } else if (!songService.getPlayingSong(roomUuid).isPresent()) {
            Optional<Song> firstQueued = songService.getNextSong(roomUuid);
            roomPlay(firstQueued.orElseThrow(() -> new NotFoundException("Queue is empty")), 0);
        }

        return songService.getSongListByRoomUuid(roomUuid);
    }

    @Override
    public List<Song> roomPause(String roomUuid) throws DatabaseException, InvalidUpdateException, InvalidArgumentException, IOException, SpotifyWebApiException {
        Optional<Song> playingOpt = songService.getPlayingSong(roomUuid);

        if (playingOpt.isPresent()) {
            Song playing = playingOpt.get();

            // Pause playback
            spotifyRequest.execRequestListAsync((spotifyApi, device) -> spotifyApi.pauseUsersPlayback().device_id(device).build(), getRoomTokenDeviceMap(roomUuid));

            // Update playing
            long oldPassedMs = playing.getCurrentMs() == null ? 0L: playing.getCurrentMs();
            long newPassedMs = ChronoUnit.MILLIS.between(playing.getPlayingTime(), TimeHelper.getLocalDateTimeNow());

            playing.setCurrentMs((int) (oldPassedMs + newPassedMs));
            playing.setSongStatus(SongStatus.PAUSED.getSongStatus());
            songService.update(playing);
        } else {
            String err = String.format("Not playing any song in Room[%s]", roomUuid);
            throw new NotFoundException(err);
        }

        return songService.getSongListByRoomUuid(roomUuid);
    }

    @Override
    public List<Song> roomNext(String roomUuid) throws DatabaseException, InvalidUpdateException, InvalidArgumentException, IOException, SpotifyWebApiException {
        Optional<Song> next = songService.getNextSong(roomUuid);

        if (next.isPresent()) {
            Optional<Song> playingOpt = songService.getPlayingSong(roomUuid);
            if (playingOpt.isPresent()) { // Set old playing as played, if present
                Song playing = playingOpt.get();
                playing.setVotes(0);
                playing.setSongStatus(SongStatus.PLAYED.getSongStatus());
                songService.update(playing);
            }
            return roomPlay(next.get(), 0);
        } else {
            throw new NotFoundException("Next song is empty");
        }
    }

    @Override
    public List<Song> roomPrevious(String roomUuid) throws DatabaseException, InvalidUpdateException, InvalidArgumentException, IOException, SpotifyWebApiException {
        Optional<Song> previous = songService.getPreviousSong(roomUuid);

        if (previous.isPresent()) {
            Optional<Song> playingOpt = songService.getPlayingSong(roomUuid);
            if (playingOpt.isPresent()) { // Set old playing as next, if present
                Song playing = playingOpt.get();
                playing.setSongStatus(SongStatus.NEXT.getSongStatus());
                songService.update(playing);
            }
            return roomPlay(previous.get(), 0);
        } else {
            throw new NotFoundException("Previous song is empty");
        }
    }

    @Override
    public int roomSeek(String roomUuid, Integer ms) throws DatabaseException, InvalidArgumentException, IOException, SpotifyWebApiException {
        Optional<Song> playingOpt = songService.getPlayingSong(roomUuid);
        Optional<Song> pausedOpt = songService.getPausedSong(roomUuid);

        if (playingOpt.isPresent() || pausedOpt.isPresent()) {
            spotifyRequest.execRequestListAsync((spotifyApi, device) -> spotifyApi.seekToPositionInCurrentlyPlayingTrack(ms).device_id(device).build(), getRoomTokenDeviceMap(roomUuid));
        } else {
            String err = String.format("Not playing or paused any song in Room[%s]", roomUuid);
            throw new NotFoundException(err);
        }

        return ms;
    }

    @Override
    public boolean roomRepeat(String roomUuid) throws DatabaseException, InvalidArgumentException, InvalidUpdateException, IOException, SpotifyWebApiException {
        Optional<Song> playingOpt = songService.getPlayingSong(roomUuid);
        Optional<Song> pausedOpt = songService.getPausedSong(roomUuid);

        if (playingOpt.isPresent()) {
            return roomRepeatHelper(roomUuid, playingOpt.get());
        } else if (pausedOpt.isPresent()) {
            return roomRepeatHelper(roomUuid, pausedOpt.get());
        } else {
            throw new NotFoundException("Song not found for repeat");
        }
    }

    private boolean roomRepeatHelper(String roomUuid, Song song) throws InvalidUpdateException, DatabaseException, InvalidArgumentException, IOException, SpotifyWebApiException {
        String repeatMode;
        Boolean repeatFlag = song.getRepeatFlag();

        if (repeatFlag == null || !repeatFlag) {
            repeatMode = ModelObjectType.TRACK.getType();
            repeatFlag = true;
        } else {
            repeatMode = "off";
            repeatFlag = false;
        }

        spotifyRequest.execRequestListAsync((spotifyApi, device) -> spotifyApi.setRepeatModeOnUsersPlayback(repeatMode).device_id(device).build(), getRoomTokenDeviceMap(roomUuid));

        song.setRepeatFlag(repeatFlag);
        songService.update(song);

        return repeatFlag;
    }

    ///////////////////////
    // User Based
    ///////////////////////

    @Override
    public boolean userSyncWithRoom(RoomUser roomUser) throws DatabaseException, IOException, SpotifyWebApiException {
        Optional<Song> playingOpt = songService.getPlayingSong(roomUser.getRoomUuid());
        Optional<Song> pausedOpt = songService.getPausedSong(roomUser.getRoomUuid());

        if (playingOpt.isPresent()) { // play currently playing song with current ms
            return play(roomUser, playingOpt.get());
        } else if (pausedOpt.isPresent()) { // seek to current ms
            return seek(roomUser, pausedOpt.get());
        } else { // noop
            return false;
        }
    }

    private boolean play(RoomUser roomUser, Song song) throws DatabaseException, IOException, SpotifyWebApiException {
        String userUuid = roomUser.getUserUuid();
        int ms = Math.toIntExact(song.getCurrentMs() + ChronoUnit.MILLIS.between(song.getPlayingTime(), TimeHelper.getLocalDateTimeNow()));

        Optional<SpotifyToken> token = tokenService.getTokenByUserUuid(userUuid);
        List<UserDevice> userDevices = userDeviceService.getUserDevicesByUserUuid(userUuid);

        if (token.isPresent() && !userDevices.isEmpty()) {
            String accessToken = token.get().getAccessToken();
            String deviceId = userDevices.get(0).getDeviceId();

            String songUri = song.getSongUri();
            List<String> songList = Collections.singletonList(songUri);
            JsonArray urisJson = new GsonBuilder().create().toJsonTree(songList).getAsJsonArray();

            spotifyRequest.execRequestAsync((spotifyApi) -> spotifyApi.startResumeUsersPlayback().uris(urisJson).position_ms(ms).device_id(deviceId).build(), accessToken);
            return true;
        }
        return false;
    }

    private boolean seek(RoomUser roomUser, Song song) throws DatabaseException, IOException, SpotifyWebApiException {
        String userUuid = roomUser.getUserUuid();
        int ms = Math.toIntExact(song.getCurrentMs());

        Optional<SpotifyToken> token = tokenService.getTokenByUserUuid(userUuid);
        List<UserDevice> userDevices = userDeviceService.getUserDevicesByUserUuid(userUuid);

        if (token.isPresent() && !userDevices.isEmpty()) {
            String accessToken = token.get().getAccessToken();
            String deviceId = userDevices.get(0).getDeviceId();

            spotifyRequest.execRequestAsync((spotifyApi) -> spotifyApi.seekToPositionInCurrentlyPlayingTrack(ms).device_id(deviceId).build(), accessToken);
            return true;
        }
        return false;
    }

    ///////////////////////
    // Model getter methods
    ///////////////////////

    private PlayerModel getRoomPlayerModel(String roomUuid) throws DatabaseException, InvalidArgumentException {
        List<String> spotifyTokenList = new LinkedList<>();
        List<String> userDeviceList = new LinkedList<>();

        List<User> userList = userService.getUsersByRoomUuid(roomUuid);

        for (User u : userList) {
            String userUuid = u.getUuid();
            Optional<SpotifyToken> token = tokenService.getTokenByUserUuid(userUuid);

            token.ifPresent(spotifyToken -> spotifyTokenList.add(spotifyToken.getAccessToken()));

            List<UserDevice> userDevices = userDeviceService.getUserDevicesByUserUuid(userUuid);
            if (!userDevices.isEmpty())
                userDeviceList.add(userDevices.get(0).getDeviceId());
        }

        PlayerModel playerModel = new PlayerModel();
        playerModel.setRoomUuid(roomUuid);
        playerModel.setSpotifyTokenList(spotifyTokenList);
        playerModel.setUserDeviceList(userDeviceList);

        return playerModel;
    }

    private HashMap<String, String> getRoomTokenDeviceMap(String roomUuid) throws DatabaseException, InvalidArgumentException {
        HashMap<String, String> map = new HashMap<>();

        List<User> userList = userService.getUsersByRoomUuid(roomUuid);

        for (User u : userList) {
            String userUuid = u.getUuid();
            Optional<SpotifyToken> token = tokenService.getTokenByUserUuid(userUuid);
            List<UserDevice> userDevices = userDeviceService.getUserDevicesByUserUuid(userUuid);

            if (token.isPresent() && !userDevices.isEmpty()) {
                map.put(token.get().getAccessToken(), userDevices.get(0).getDeviceId());
            }
        }

        return map;
    }

    private HashMap<String, String> getUserTokenDeviceMap(String userUuid) throws DatabaseException {
        HashMap<String, String> map = new HashMap<>();

        Optional<SpotifyToken> token = tokenService.getTokenByUserUuid(userUuid);
        List<UserDevice> userDevices = userDeviceService.getUserDevicesByUserUuid(userUuid);

        if (token.isPresent() && !userDevices.isEmpty()) {
            map.put(token.get().getAccessToken(), userDevices.get(0).getDeviceId());
        }

        return map;
    }

}
