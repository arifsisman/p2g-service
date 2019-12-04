package vip.yazilim.p2g.web.service.spotify.impl;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.wrapper.spotify.enums.ModelObjectType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.constant.QueueStatus;
import vip.yazilim.p2g.web.entity.SpotifyToken;
import vip.yazilim.p2g.web.entity.relation.RoomQueue;
import vip.yazilim.p2g.web.entity.relation.UserDevice;
import vip.yazilim.p2g.web.exception.PlayerException;
import vip.yazilim.p2g.web.exception.QueueException;
import vip.yazilim.p2g.web.exception.RequestException;
import vip.yazilim.p2g.web.service.p2g.ITokenService;
import vip.yazilim.p2g.web.service.p2g.relation.IRoomQueueService;
import vip.yazilim.p2g.web.service.p2g.relation.IUserDeviceService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyPlayerService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyRequestService;
import vip.yazilim.spring.utils.exception.DatabaseException;
import vip.yazilim.spring.utils.exception.InvalidUpdateException;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class SpotifyPlayerService implements ISpotifyPlayerService {

    @Autowired
    private ITokenService tokenService;

    @Autowired
    private ISpotifyRequestService spotifyRequest;

    @Autowired
    private IUserDeviceService userDeviceService;

    @Autowired
    private IRoomQueueService roomQueueService;

    @Override
    public List<RoomQueue> play(String roomQueueUuid) throws RequestException, PlayerException, DatabaseException, QueueException, InvalidUpdateException {
        RoomQueue nowPlaying = roomQueueService.getById(roomQueueUuid).orElseThrow(() -> new PlayerException("Queued song not found"));

        String songUri = nowPlaying.getSongUri();
        String roomUuid = nowPlaying.getRoomUuid();

        if (!songUri.contains(ModelObjectType.TRACK.getType())) {
            String err = String.format("URI[%s] does not match with an Track URI", songUri);
            throw new PlayerException(err);
        }

        // JsonArray with song, because uris needs JsonArray as input
        List<String> songList = Collections.singletonList(songUri);
        JsonArray urisJson = new GsonBuilder().create().toJsonTree(songList).getAsJsonArray();

        // Start playback
        List<SpotifyToken> spotifyTokenList = tokenService.getTokenListByRoomUuid(roomUuid);
        List<UserDevice> userDeviceList = userDeviceService.getUserDevicesByRoomUuid(roomUuid);
        spotifyRequest.execRequestListAsync((spotifyApi, device) -> spotifyApi.startResumeUsersPlayback().uris(urisJson).device_id(device).build(), spotifyTokenList, userDeviceList);

        // Update queue status
        roomQueueService.updateRoomQueueStatus(nowPlaying);

        // Update nowPlaying
        nowPlaying.setPlayingTime(new Date());
        roomQueueService.update(nowPlaying);

        return roomQueueService.getRoomQueueListByRoomUuid(roomUuid);
    }

    @Override
    public List<RoomQueue> pause(String roomUuid) throws RequestException, DatabaseException, InvalidUpdateException, PlayerException {
        RoomQueue nowPlaying = roomQueueService.getRoomQueueNowPlaying(roomUuid);

        if (nowPlaying == null || !(nowPlaying.getQueueStatus().equals(QueueStatus.PLAYING.getQueueStatus()))) {
            String err = String.format("Not playing any song in Room[%s]", roomUuid);
            throw new PlayerException(err);
        }

        // Pause playback
        List<SpotifyToken> spotifyTokenList = tokenService.getTokenListByRoomUuid(roomUuid);
        List<UserDevice> userDeviceList = userDeviceService.getUserDevicesByRoomUuid(roomUuid);
        spotifyRequest.execRequestListAsync((spotifyApi, device) -> spotifyApi.pauseUsersPlayback().device_id(device).build(), spotifyTokenList, userDeviceList);

        // Update nowPlaying
        nowPlaying.setCurrentMs(System.currentTimeMillis() - nowPlaying.getPlayingTime().getTime());
        nowPlaying.setQueueStatus(QueueStatus.PAUSED.getQueueStatus());
        roomQueueService.update(nowPlaying);

        return roomQueueService.getRoomQueueListByRoomUuid(roomUuid);
    }

    @Override
    public List<RoomQueue> resume(String roomUuid) throws RequestException, DatabaseException, InvalidUpdateException, PlayerException {
        RoomQueue paused = roomQueueService.getRoomQueuePaused(roomUuid);

        if (paused == null) {
            String err = String.format("Not paused any song in Room[%s]", roomUuid);
            throw new PlayerException(err);
        }

        int currentMs = Math.toIntExact(paused.getCurrentMs());

        // Resume playback
        List<SpotifyToken> spotifyTokenList = tokenService.getTokenListByRoomUuid(roomUuid);
        List<UserDevice> userDeviceList = userDeviceService.getUserDevicesByRoomUuid(roomUuid);
        spotifyRequest.execRequestListAsync((spotifyApi, device) -> spotifyApi.startResumeUsersPlayback().device_id(device).position_ms(currentMs).build(), spotifyTokenList, userDeviceList);

        // Update nowPlaying
        paused.setQueueStatus(QueueStatus.PLAYING.getQueueStatus());
        roomQueueService.update(paused);

        return roomQueueService.getRoomQueueListByRoomUuid(roomUuid);
    }

    @Override
    public List<RoomQueue> next(String roomUuid) throws RequestException, DatabaseException, PlayerException, QueueException, InvalidUpdateException {
        RoomQueue next = roomQueueService.getRoomQueueNext(roomUuid);
        if (next == null) {
            throw new PlayerException("Next song is empty.");
        } else {
            play(next.getUuid());
        }

        return roomQueueService.getRoomQueueListByRoomUuid(roomUuid);
    }

    @Override
    public List<RoomQueue> previous(String roomUuid) throws RequestException, DatabaseException, PlayerException, QueueException, InvalidUpdateException {
        RoomQueue previous = roomQueueService.getRoomQueuePrevious(roomUuid);
        if (previous == null) {
            throw new PlayerException("Previous song is empty.");
        } else {
            play(previous.getUuid());
        }

        return roomQueueService.getRoomQueueListByRoomUuid(roomUuid);
    }

    @Override
    public List<RoomQueue> seek(String roomUuid, Integer ms) throws RequestException, DatabaseException, PlayerException {
        RoomQueue nowPlaying = roomQueueService.getRoomQueueNowPlaying(roomUuid);
        RoomQueue paused = roomQueueService.getRoomQueuePaused(roomUuid);

        if (nowPlaying == null && paused == null) {
            String err = String.format("Not playing or paused any song in Room[%s]", roomUuid);
            throw new PlayerException(err);
        }

        List<SpotifyToken> spotifyTokenList = tokenService.getTokenListByRoomUuid(roomUuid);
        List<UserDevice> userDeviceList = userDeviceService.getUserDevicesByRoomUuid(roomUuid);
        spotifyRequest.execRequestListAsync((spotifyApi, device) -> spotifyApi.seekToPositionInCurrentlyPlayingTrack(ms).device_id(device).build(), spotifyTokenList, userDeviceList);

        return roomQueueService.getRoomQueueListByRoomUuid(roomUuid);
    }

    @Override
    public List<RoomQueue> repeat(String roomUuid) throws RequestException, DatabaseException, PlayerException {
        RoomQueue nowPlaying = roomQueueService.getRoomQueueNowPlaying(roomUuid);

        if (nowPlaying == null) {
            String err = String.format("Not playing any song in Room[%s]", roomUuid);
            throw new PlayerException(err);
        }

        List<SpotifyToken> spotifyTokenList = tokenService.getTokenListByRoomUuid(roomUuid);
        List<UserDevice> userDeviceList = userDeviceService.getUserDevicesByRoomUuid(roomUuid);
        spotifyRequest.execRequestListAsync((spotifyApi, device) -> spotifyApi.setRepeatModeOnUsersPlayback(ModelObjectType.TRACK.getType()).device_id(device).build(), spotifyTokenList, userDeviceList);

        return roomQueueService.getRoomQueueListByRoomUuid(roomUuid);
    }

}
