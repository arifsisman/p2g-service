package vip.yazilim.p2g.web.service.spotify.impl;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.wrapper.spotify.enums.ModelObjectType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.constant.SongStatus;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.entity.relation.Song;
import vip.yazilim.p2g.web.entity.relation.SpotifyToken;
import vip.yazilim.p2g.web.entity.relation.UserDevice;
import vip.yazilim.p2g.web.exception.SpotifyException;
import vip.yazilim.p2g.web.service.p2g.ITokenService;
import vip.yazilim.p2g.web.service.p2g.IUserService;
import vip.yazilim.p2g.web.service.p2g.relation.ISongService;
import vip.yazilim.p2g.web.service.p2g.relation.IUserDeviceService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyPlayerService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyRequestService;
import vip.yazilim.p2g.web.service.spotify.model.PlayerModel;
import vip.yazilim.spring.core.exception.general.InvalidArgumentException;
import vip.yazilim.spring.core.exception.general.InvalidUpdateException;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;
import vip.yazilim.spring.core.exception.web.NotFoundException;

import javax.transaction.Transactional;
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

    @Override
    public List<Song> play(Song song, int ms) throws SpotifyException, DatabaseException, InvalidUpdateException, InvalidArgumentException {
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
            spotifyRequest.execRequestListAsync((spotifyApi, device) -> spotifyApi.startResumeUsersPlayback().uris(urisJson).position_ms(ms).device_id(device).build(), getTokenDeviceMap(roomUuid));


            // Update playing
            song.setPlayingTime(new Date());
            song.setSongStatus(SongStatus.PLAYING.getSongStatus());
            songService.update(song);
        }

        return songService.getSongListByRoomUuid(roomUuid);
    }

    @Override
    public List<Song> startResume(String roomUuid) throws SpotifyException, DatabaseException, InvalidUpdateException, InvalidArgumentException {
        Optional<Song> pausedOpt = songService.getPausedSong(roomUuid);

        if (pausedOpt.isPresent()) {
            Song paused = pausedOpt.get();
            int currentMs = Math.toIntExact(paused.getCurrentMs());

            // Resume playback
            play(paused, currentMs);

            // Update playing
            paused.setSongStatus(SongStatus.PLAYING.getSongStatus());
            songService.update(paused);
        } else if (!songService.getPlayingSong(roomUuid).isPresent()) {
            Optional<Song> firstQueued = songService.getNextSong(roomUuid);
            play(firstQueued.orElseThrow(() -> new NotFoundException("Queue is empty")), 0);
        }

        return songService.getSongListByRoomUuid(roomUuid);
    }

    @Override
    public List<Song> pause(String roomUuid) throws SpotifyException, DatabaseException, InvalidUpdateException, InvalidArgumentException {
        Optional<Song> playingOpt = songService.getPlayingSong(roomUuid);

        if (playingOpt.isPresent()) {
            Song playing = playingOpt.get();

            // Pause playback
            spotifyRequest.execRequestListAsync((spotifyApi, device) -> spotifyApi.pauseUsersPlayback().device_id(device).build(), getTokenDeviceMap(roomUuid));

            // Update playing
            playing.setCurrentMs(System.currentTimeMillis() - playing.getPlayingTime().getTime());
            playing.setSongStatus(SongStatus.PAUSED.getSongStatus());
            songService.update(playing);
        } else {
            String err = String.format("Not playing any song in Room[%s]", roomUuid);
            throw new NotFoundException(err);
        }

        return songService.getSongListByRoomUuid(roomUuid);
    }

    @Override
    public List<Song> next(String roomUuid) throws SpotifyException, DatabaseException, InvalidUpdateException, InvalidArgumentException {
        Optional<Song> next = songService.getNextSong(roomUuid);

        if (next.isPresent()) {
            Optional<Song> playingOpt = songService.getPlayingSong(roomUuid);
            if (playingOpt.isPresent()) { // Set old playing as played, if present
                Song playing = playingOpt.get();
                playing.setVotes(0);
                playing.setSongStatus(SongStatus.PLAYED.getSongStatus());
                songService.update(playing);
            }
            return play(next.get(), 0);
        } else {
            throw new NotFoundException("Next song is empty");
        }
    }

    @Override
    public List<Song> previous(String roomUuid) throws SpotifyException, DatabaseException, InvalidUpdateException, InvalidArgumentException {
        Optional<Song> previous = songService.getPreviousSong(roomUuid);

        if (previous.isPresent()) {
            Optional<Song> playingOpt = songService.getPlayingSong(roomUuid);
            if (playingOpt.isPresent()) { // Set old playing as next, if present
                Song playing = playingOpt.get();
                playing.setSongStatus(SongStatus.NEXT.getSongStatus());
                songService.update(playing);
            }
            return play(previous.get(), 0);
        } else {
            throw new NotFoundException("Previous song is empty");
        }
    }

    @Override
    public int seek(String roomUuid, Integer ms) throws SpotifyException, DatabaseException, InvalidArgumentException {
        Optional<Song> playingOpt = songService.getPlayingSong(roomUuid);
        Optional<Song> pausedOpt = songService.getPausedSong(roomUuid);

        if (playingOpt.isPresent() || pausedOpt.isPresent()) {
            spotifyRequest.execRequestListAsync((spotifyApi, device) -> spotifyApi.seekToPositionInCurrentlyPlayingTrack(ms).device_id(device).build(), getTokenDeviceMap(roomUuid));
        } else {
            String err = String.format("Not playing or paused any song in Room[%s]", roomUuid);
            throw new NotFoundException(err);
        }

        return ms;
    }

    @Override
    public boolean repeat(String roomUuid) throws SpotifyException, DatabaseException, InvalidArgumentException, InvalidUpdateException {
        Optional<Song> playingOpt = songService.getPlayingSong(roomUuid);
        Boolean repeatFlag;

        if (playingOpt.isPresent()) {
            Song playing = playingOpt.get();
            String repeatMode;
            repeatFlag = playing.getRepeatFlag();

            if (repeatFlag == null || !repeatFlag) {
                repeatMode = ModelObjectType.TRACK.getType();
                repeatFlag = true;
            } else {
                repeatMode = "off";
                repeatFlag = false;
            }

            spotifyRequest.execRequestListAsync((spotifyApi, device) -> spotifyApi.setRepeatModeOnUsersPlayback(repeatMode).device_id(device).build(), getTokenDeviceMap(roomUuid));

            playing.setRepeatFlag(repeatFlag);
            songService.update(playing);
        } else {
            String err = String.format("Not playing any song in Room[%s]", roomUuid);
            throw new NotFoundException(err);
        }

        return repeatFlag;
    }

    private PlayerModel getPlayerModel(String roomUuid) throws DatabaseException, InvalidArgumentException {
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

    private HashMap<String, String> getTokenDeviceMap(String roomUuid) throws DatabaseException, InvalidArgumentException {
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

}
