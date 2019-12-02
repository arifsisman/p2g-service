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
        spotifyRequest.execRequestListSync((spotifyApi, device) -> spotifyApi.startResumeUsersPlayback().uris(urisJson).device_id(device).build(), spotifyTokenList, userDeviceList);

        roomQueueService.updateQueueStatus(roomQueue);

        roomQueue.setPlayingTime(new Date());
        roomQueueService.update(roomQueue);
    }

    @Override
    public void resume(RoomQueue roomQueue) throws RequestException, DatabaseException {
        String roomUuid = roomQueue.getRoomUuid();
        List<SpotifyToken> spotifyTokenList = tokenService.getTokenListByRoomUuid(roomUuid);
        List<UserDevice> userDeviceList = userDeviceService.getUserDevicesByRoomUuid(roomUuid);
        spotifyRequest.execRequestListSync((spotifyApi, device) -> spotifyApi.startResumeUsersPlayback().device_id(device).build(), spotifyTokenList, userDeviceList);
    }

    @Override
    public void pause(RoomQueue roomQueue) throws RequestException, DatabaseException, InvalidUpdateException {
        String roomUuid = roomQueue.getRoomUuid();
        List<SpotifyToken> spotifyTokenList = tokenService.getTokenListByRoomUuid(roomUuid);
        List<UserDevice> userDeviceList = userDeviceService.getUserDevicesByRoomUuid(roomUuid);
        spotifyRequest.execRequestListSync((spotifyApi, device) -> spotifyApi.pauseUsersPlayback().device_id(device).build(), spotifyTokenList, userDeviceList);

        roomQueue.setCurrentMs(new Date().getTime() - roomQueue.getPlayingTime().getTime());
        roomQueueService.update(roomQueue);
    }

    @Override
    public void next(RoomQueue roomQueue) throws RequestException, DatabaseException, PlayerException, QueueException, InvalidUpdateException {
        String roomUuid = roomQueue.getRoomUuid();
        List<RoomQueue> roomQueueList = roomQueueService.getQueueListByRoomUuidAndStatus(roomUuid, QueueStatus.NEXT);

        if (roomQueueList.size() < 1) {
            throw new PlayerException("Queue is empty.");
        } else {
            int index = roomQueueList.indexOf(roomQueue);
            play(roomQueueList.get(index + 1));
        }
    }

    @Override
    public void previous(RoomQueue roomQueue) throws RequestException, DatabaseException, PlayerException, QueueException, InvalidUpdateException {
        String roomUuid = roomQueue.getRoomUuid();
        List<RoomQueue> roomQueueList = roomQueueService.getQueueListByRoomUuidAndStatus(roomUuid, QueueStatus.PREVIOUS);

        if (roomQueueList.size() < 1) {
            throw new PlayerException("Queue is empty.");
        } else {
            int index = roomQueueList.indexOf(roomQueue);
            play(roomQueueList.get(index - 1));
        }
    }

    @Override
    public void seek(RoomQueue roomQueue, Integer ms) throws RequestException, DatabaseException {
        String roomUuid = roomQueue.getRoomUuid();
        List<SpotifyToken> spotifyTokenList = tokenService.getTokenListByRoomUuid(roomUuid);
        List<UserDevice> userDeviceList = userDeviceService.getUserDevicesByRoomUuid(roomUuid);
        spotifyRequest.execRequestListSync((spotifyApi, device) -> spotifyApi.seekToPositionInCurrentlyPlayingTrack(ms).device_id(device).build(), spotifyTokenList, userDeviceList);
    }

    @Override
    public void repeat(RoomQueue roomQueue) throws RequestException, DatabaseException {
        String roomUuid = roomQueue.getRoomUuid();
        List<SpotifyToken> spotifyTokenList = tokenService.getTokenListByRoomUuid(roomUuid);
        List<UserDevice> userDeviceList = userDeviceService.getUserDevicesByRoomUuid(roomUuid);
        spotifyRequest.execRequestListSync((spotifyApi, device) -> spotifyApi.setRepeatModeOnUsersPlayback(ModelObjectType.TRACK.getType()).device_id(device).build(), spotifyTokenList, userDeviceList);
    }

}
