package vip.yazilim.p2g.web.service.spotify.impl;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.wrapper.spotify.enums.ModelObjectType;
import com.wrapper.spotify.model_objects.miscellaneous.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.SpotifyToken;
import vip.yazilim.p2g.web.entity.relation.UserDevice;
import vip.yazilim.p2g.web.exception.PlayerException;
import vip.yazilim.p2g.web.exception.RequestException;
import vip.yazilim.p2g.web.exception.TokenException;
import vip.yazilim.p2g.web.service.p2g.ITokenService;
import vip.yazilim.p2g.web.service.p2g.relation.IUserDeviceService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyPlayerService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyRequestService;
import vip.yazilim.spring.utils.exception.DatabaseException;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class SpotifyPlayerService implements ISpotifyPlayerService {

    private final Logger LOGGER = LoggerFactory.getLogger(SpotifyPlayerService.class);

    @Autowired
    private ITokenService tokenService;

    @Autowired
    private ISpotifyRequestService spotifyRequest;

    @Autowired
    private IUserDeviceService userDeviceService;


    @Override
    public void play(String roomUuid, String songUri) throws RequestException, PlayerException, DatabaseException {
        if (!songUri.contains(ModelObjectType.TRACK.getType())) {
            String msg = String.format("URI[%s] does not match with an Track URI!", songUri);
            throw new PlayerException(msg);
        }

        // JsonArray with song, because uris endpoint needs JsonArray as input
        List<String> songList = Collections.singletonList(songUri);
        JsonArray urisJson = new GsonBuilder().create().toJsonTree(songList).getAsJsonArray();

        List<SpotifyToken> spotifyTokenList = tokenService.getTokenListByRoomUuid(roomUuid);
        List<UserDevice> userDeviceList = userDeviceService.getDevicesByRoomUuid(roomUuid);
        //TODO: generalize for users in room -> .device_id("f9527474526ac6bc2ddbdd539fb69c82187621fa")
        spotifyRequest.execRequestListSync((spotifyApi, device) -> spotifyApi.startResumeUsersPlayback().uris(urisJson).device_id(device).build(), spotifyTokenList);
    }

    @Override
    public void play(String roomUuid) throws RequestException, DatabaseException {
        List<SpotifyToken> spotifyTokenList = tokenService.getTokenListByRoomUuid(roomUuid);
        List<UserDevice> userDeviceList = userDeviceService.getDevicesByRoomUuid(roomUuid);
        spotifyRequest.execRequestListSync((spotifyApi, device) -> spotifyApi.startResumeUsersPlayback().device_id(device).build(), spotifyTokenList);
    }

    @Override
    public void pause(String roomUuid) throws RequestException, DatabaseException {
        List<SpotifyToken> spotifyTokenList = tokenService.getTokenListByRoomUuid(roomUuid);
        List<UserDevice> userDeviceList = userDeviceService.getDevicesByRoomUuid(roomUuid);
        spotifyRequest.execRequestListSync((spotifyApi, device) -> spotifyApi.pauseUsersPlayback().device_id(device).build(), spotifyTokenList);
    }

    @Override
    public void next(String roomUuid) throws RequestException, DatabaseException {
        List<SpotifyToken> spotifyTokenList = tokenService.getTokenListByRoomUuid(roomUuid);
        List<UserDevice> userDeviceList = userDeviceService.getDevicesByRoomUuid(roomUuid);
        spotifyRequest.execRequestListSync((spotifyApi, device) -> spotifyApi.skipUsersPlaybackToNextTrack().device_id(device).build(), spotifyTokenList);
    }

    @Override
    public void previous(String roomUuid) throws RequestException, DatabaseException {
        List<SpotifyToken> spotifyTokenList = tokenService.getTokenListByRoomUuid(roomUuid);
        List<UserDevice> userDeviceList = userDeviceService.getDevicesByRoomUuid(roomUuid);
        spotifyRequest.execRequestListSync((spotifyApi, device) -> spotifyApi.skipUsersPlaybackToPreviousTrack().device_id(device).build(), spotifyTokenList);
    }

    @Override
    public void seek(String roomUuid, Integer ms) throws RequestException, DatabaseException {
        List<SpotifyToken> spotifyTokenList = tokenService.getTokenListByRoomUuid(roomUuid);
        List<UserDevice> userDeviceList = userDeviceService.getDevicesByRoomUuid(roomUuid);
        spotifyRequest.execRequestListSync((spotifyApi, device) -> spotifyApi.seekToPositionInCurrentlyPlayingTrack(ms).device_id(device).build(), spotifyTokenList);
    }

    @Override
    public void repeat(String roomUuid) throws RequestException, DatabaseException {
        List<SpotifyToken> spotifyTokenList = tokenService.getTokenListByRoomUuid(roomUuid);
        List<UserDevice> userDeviceList = userDeviceService.getDevicesByRoomUuid(roomUuid);
        spotifyRequest.execRequestListSync((spotifyApi, device) -> spotifyApi.setRepeatModeOnUsersPlayback(ModelObjectType.TRACK.getType()).device_id(device).build(), spotifyTokenList);

    }

    @Override
    public List<UserDevice> getUsersAvailableDevices(String userUuid) throws DatabaseException, TokenException, RequestException {
        List<UserDevice> userDeviceList = new LinkedList<>();
        SpotifyToken spotifyToken;

        spotifyToken = tokenService.getTokenByUserUuid(userUuid).orElseThrow(() -> new TokenException("Token not found"));

        Device[] devices = spotifyRequest.execRequestSync((spotifyApi) -> spotifyApi.getUsersAvailableDevices().build(), spotifyToken);

        for (Device d : devices) {
            UserDevice userDevice = new UserDevice();

            userDevice.setUserUuid(userUuid);
            userDevice.setDeviceId(d.getId());
            userDevice.setDeviceName(d.getName());
            userDevice.setDeviceName(d.getName());
            userDevice.setActiveFlag(d.getIs_active());
            userDevice.setDeviceType(d.getType());

            userDeviceList.add(userDevice);
        }

        return userDeviceList;
    }
}
