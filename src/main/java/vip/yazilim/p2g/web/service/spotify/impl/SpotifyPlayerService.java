package vip.yazilim.p2g.web.service.spotify.impl;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.wrapper.spotify.enums.ModelObjectType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.constant.QueueStatus;
import vip.yazilim.p2g.web.entity.SpotifyToken;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.entity.relation.RoomQueue;
import vip.yazilim.p2g.web.entity.relation.UserDevice;
import vip.yazilim.p2g.web.exception.PlayerException;
import vip.yazilim.p2g.web.exception.QueueException;
import vip.yazilim.p2g.web.exception.RequestException;
import vip.yazilim.p2g.web.service.p2g.ITokenService;
import vip.yazilim.p2g.web.service.p2g.IUserService;
import vip.yazilim.p2g.web.service.p2g.relation.IRoomQueueService;
import vip.yazilim.p2g.web.service.p2g.relation.IUserDeviceService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyPlayerService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyRequestService;
import vip.yazilim.p2g.web.service.spotify.model.PlayerModel;
import vip.yazilim.spring.core.exception.InvalidArgumentException;
import vip.yazilim.spring.core.exception.InvalidUpdateException;
import vip.yazilim.spring.core.exception.database.DatabaseException;

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
    private IRoomQueueService roomQueueService;

    @Override
    public List<RoomQueue> play(String roomQueueUuid) throws RequestException, PlayerException, DatabaseException, QueueException, InvalidUpdateException, InvalidArgumentException {
        RoomQueue nowPlaying = roomQueueService.getById(roomQueueUuid).orElseThrow(() -> new PlayerException("Queued song not found"));

        String songUri = nowPlaying.getSongUri();
        String roomUuid = nowPlaying.getRoomUuid();

        if (!songUri.contains(ModelObjectType.TRACK.getType())) {
            String err = String.format("URI[%s] does not match with an Track URI", songUri);
            throw new PlayerException(err);
        } else {
            // JsonArray with song, because uris needs JsonArray as input
            List<String> songList = Collections.singletonList(songUri);
            JsonArray urisJson = new GsonBuilder().create().toJsonTree(songList).getAsJsonArray();

            // Start playback
            spotifyRequest.execRequestListAsync((spotifyApi, device) -> spotifyApi.startResumeUsersPlayback().uris(urisJson).device_id(device).build(), getPlayerModel(roomUuid));

            // Update queue status
            roomQueueService.updateRoomQueueStatus(nowPlaying);

            // Update nowPlaying
            nowPlaying.setPlayingTime(new Date());
            roomQueueService.update(nowPlaying);
        }

        return roomQueueService.getRoomQueueListByRoomUuid(roomUuid);
    }

    @Override
    public List<RoomQueue> pause(String roomUuid) throws RequestException, DatabaseException, InvalidUpdateException, PlayerException, InvalidArgumentException {
        RoomQueue nowPlaying = roomQueueService.getRoomQueueNowPlaying(roomUuid);

        if (nowPlaying == null || !(nowPlaying.getQueueStatus().equals(QueueStatus.PLAYING.getQueueStatus()))) {
            String err = String.format("Not playing any song in Room[%s]", roomUuid);
            throw new PlayerException(err);
        } else {
            // Pause playback
            spotifyRequest.execRequestListAsync((spotifyApi, device) -> spotifyApi.pauseUsersPlayback().device_id(device).build(), getPlayerModel(roomUuid));

            // Update nowPlaying
            nowPlaying.setCurrentMs(System.currentTimeMillis() - nowPlaying.getPlayingTime().getTime());
            nowPlaying.setQueueStatus(QueueStatus.PAUSED.getQueueStatus());
            roomQueueService.update(nowPlaying);
        }

        return roomQueueService.getRoomQueueListByRoomUuid(roomUuid);
    }

    @Override
    public List<RoomQueue> resume(String roomUuid) throws RequestException, DatabaseException, InvalidUpdateException, PlayerException, InvalidArgumentException {
        RoomQueue paused = roomQueueService.getRoomQueuePaused(roomUuid);

        if (paused == null) {
            String err = String.format("Not paused any song in Room[%s]", roomUuid);
            throw new PlayerException(err);
        } else {
            int currentMs = Math.toIntExact(paused.getCurrentMs());

            // Resume playback
            spotifyRequest.execRequestListAsync((spotifyApi, device) -> spotifyApi.startResumeUsersPlayback().device_id(device).position_ms(currentMs).build(), getPlayerModel(roomUuid));

            // Update nowPlaying
            paused.setQueueStatus(QueueStatus.PLAYING.getQueueStatus());
            roomQueueService.update(paused);
        }

        return roomQueueService.getRoomQueueListByRoomUuid(roomUuid);
    }

    @Override
    public List<RoomQueue> next(String roomUuid) throws RequestException, DatabaseException, PlayerException, QueueException, InvalidUpdateException, InvalidArgumentException {
        RoomQueue next = roomQueueService.getRoomQueueNext(roomUuid);
        if (next == null) {
            throw new PlayerException("Next song is empty.");
        } else {
            play(next.getUuid());
        }

        return roomQueueService.getRoomQueueListByRoomUuid(roomUuid);
    }

    @Override
    public List<RoomQueue> previous(String roomUuid) throws RequestException, DatabaseException, PlayerException, QueueException, InvalidUpdateException, InvalidArgumentException {
        RoomQueue previous = roomQueueService.getRoomQueuePrevious(roomUuid);
        if (previous == null) {
            throw new PlayerException("Previous song is empty.");
        } else {
            play(previous.getUuid());
        }

        return roomQueueService.getRoomQueueListByRoomUuid(roomUuid);
    }

    @Override
    public List<RoomQueue> seek(String roomUuid, Integer ms) throws RequestException, DatabaseException, PlayerException, InvalidArgumentException {
        RoomQueue nowPlaying = roomQueueService.getRoomQueueNowPlaying(roomUuid);
        RoomQueue paused = roomQueueService.getRoomQueuePaused(roomUuid);

        if (nowPlaying == null && paused == null) {
            String err = String.format("Not playing or paused any song in Room[%s]", roomUuid);
            throw new PlayerException(err);
        } else {
            spotifyRequest.execRequestListAsync((spotifyApi, device) -> spotifyApi.seekToPositionInCurrentlyPlayingTrack(ms).device_id(device).build(), getPlayerModel(roomUuid));
        }

        return roomQueueService.getRoomQueueListByRoomUuid(roomUuid);
    }

    //TODO: implement for off repeat mode
    @Override
    public List<RoomQueue> repeat(String roomUuid) throws RequestException, DatabaseException, PlayerException, InvalidArgumentException {
        RoomQueue nowPlaying = roomQueueService.getRoomQueueNowPlaying(roomUuid);

        if (nowPlaying == null) {
            String err = String.format("Not playing any song in Room[%s]", roomUuid);
            throw new PlayerException(err);
        } else {
            spotifyRequest.execRequestListAsync((spotifyApi, device) -> spotifyApi.setRepeatModeOnUsersPlayback(ModelObjectType.TRACK.getType()).device_id(device).build(), getPlayerModel(roomUuid));
        }

        return roomQueueService.getRoomQueueListByRoomUuid(roomUuid);
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

}
