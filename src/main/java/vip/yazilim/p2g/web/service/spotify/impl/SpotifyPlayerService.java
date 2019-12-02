package vip.yazilim.p2g.web.service.spotify.impl;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.wrapper.spotify.enums.ModelObjectType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
    public void play(RoomQueue roomQueue) throws RequestException, PlayerException, DatabaseException, QueueException, InvalidUpdateException {
        String songUri = roomQueue.getSongUri();
        String roomUuid = roomQueue.getRoomUuid();

        if (!songUri.contains(ModelObjectType.TRACK.getType())) {
            String msg = String.format("URI[%s] does not match with an Track URI", songUri);
            throw new PlayerException(msg);
        }

        // JsonArray with song, because uris needs JsonArray as input
        List<String> songList = Collections.singletonList(songUri);
        JsonArray urisJson = new GsonBuilder().create().toJsonTree(songList).getAsJsonArray();

        List<SpotifyToken> spotifyTokenList = tokenService.getTokenListByRoomUuid(roomUuid);
        List<UserDevice> userDeviceList = userDeviceService.getUserDevicesByRoomUuid(roomUuid);
        spotifyRequest.execRequestListAsync((spotifyApi, device) -> spotifyApi.startResumeUsersPlayback().uris(urisJson).device_id(device).build(), spotifyTokenList, userDeviceList);

        roomQueueService.updateQueueStatus(roomQueue);

        roomQueue.setPlayingTime(new Date());
        roomQueue.setPlayingFlag(true);
        roomQueueService.update(roomQueue);
    }

    @Override
    public void pause(String roomUuid) throws RequestException, DatabaseException, InvalidUpdateException {
        List<SpotifyToken> spotifyTokenList = tokenService.getTokenListByRoomUuid(roomUuid);
        List<UserDevice> userDeviceList = userDeviceService.getUserDevicesByRoomUuid(roomUuid);
        spotifyRequest.execRequestListAsync((spotifyApi, device) -> spotifyApi.pauseUsersPlayback().device_id(device).build(), spotifyTokenList, userDeviceList);

        RoomQueue roomQueue = roomQueueService.getRoomQueueNowPlaying(roomUuid);
        roomQueue.setCurrentMs(new Date().getTime() - roomQueue.getPlayingTime().getTime());
        roomQueue.setPlayingFlag(false);
        roomQueueService.update(roomQueue);
    }

    @Override
    public void resume(String roomUuid) throws RequestException, DatabaseException, InvalidUpdateException {
        List<SpotifyToken> spotifyTokenList = tokenService.getTokenListByRoomUuid(roomUuid);
        List<UserDevice> userDeviceList = userDeviceService.getUserDevicesByRoomUuid(roomUuid);
        spotifyRequest.execRequestListAsync((spotifyApi, device) -> spotifyApi.startResumeUsersPlayback().device_id(device).build(), spotifyTokenList, userDeviceList);

        RoomQueue roomQueue = roomQueueService.getRoomQueueNowPlaying(roomUuid);
        roomQueue.setPlayingFlag(true);
        roomQueueService.update(roomQueue);
    }

    @Override
    public void next(String roomUuid) throws RequestException, DatabaseException, PlayerException, QueueException, InvalidUpdateException {

        RoomQueue next = roomQueueService.getRoomQueueNext(roomUuid);
        if (next == null) {
            throw new PlayerException("Next song is empty.");
        } else {
            play(next);
        }
    }

    @Override
    public void previous(String roomUuid) throws RequestException, DatabaseException, PlayerException, QueueException, InvalidUpdateException {
        RoomQueue previous = roomQueueService.getRoomQueuePrevious(roomUuid);
        if (previous == null) {
            throw new PlayerException("Previous song is empty.");
        } else {
            play(previous);
        }
    }

    @Override
    public void seek(String roomUuid, Integer ms) throws RequestException, DatabaseException {
        List<SpotifyToken> spotifyTokenList = tokenService.getTokenListByRoomUuid(roomUuid);
        List<UserDevice> userDeviceList = userDeviceService.getUserDevicesByRoomUuid(roomUuid);
        spotifyRequest.execRequestListAsync((spotifyApi, device) -> spotifyApi.seekToPositionInCurrentlyPlayingTrack(ms).device_id(device).build(), spotifyTokenList, userDeviceList);
    }

    @Override
    public void repeat(String roomUuid) throws RequestException, DatabaseException {
        List<SpotifyToken> spotifyTokenList = tokenService.getTokenListByRoomUuid(roomUuid);
        List<UserDevice> userDeviceList = userDeviceService.getUserDevicesByRoomUuid(roomUuid);
        spotifyRequest.execRequestListAsync((spotifyApi, device) -> spotifyApi.setRepeatModeOnUsersPlayback(ModelObjectType.TRACK.getType()).device_id(device).build(), spotifyTokenList, userDeviceList);
    }

}
