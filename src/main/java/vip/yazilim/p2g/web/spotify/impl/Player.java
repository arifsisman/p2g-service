package vip.yazilim.p2g.web.spotify.impl;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.enums.ModelObjectType;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.miscellaneous.Device;
import com.wrapper.spotify.requests.data.AbstractDataRequest;
import com.wrapper.spotify.requests.data.player.GetUsersAvailableDevicesRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.SpotifyToken;
import vip.yazilim.p2g.web.entity.relation.UserDevice;
import vip.yazilim.p2g.web.service.ITokenService;
import vip.yazilim.p2g.web.spotify.ARequestBuilder;
import vip.yazilim.p2g.web.spotify.IPlayer;
import vip.yazilim.p2g.web.spotify.IRequest;
import vip.yazilim.spring.utils.exception.DatabaseException;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class Player implements IPlayer {

    private final Logger LOGGER = LoggerFactory.getLogger(Player.class);

    @Autowired
    private ITokenService tokenService;

    @Autowired
    private IRequest spotifyRequest;

    @Override
    public void play(String roomUuid, String songUri) {

        if (!songUri.contains(ModelObjectType.TRACK.getType())) {
            LOGGER.warn("URI[{}] does not match with an song URI", songUri);
            return;
        }

        List<SpotifyToken> spotifyTokenList = tokenService.getTokenListByRoomUuid(roomUuid);

        ARequestBuilder request = new ARequestBuilder() {
            @Override
            public AbstractDataRequest build(SpotifyApi spotifyApi) {
                return spotifyApi.startResumeUsersPlayback().context_uri(songUri).build();
            }
        };

        List<AbstractDataRequest> requestList = spotifyRequest.initRequestList(spotifyTokenList, request);
        spotifyRequest.execRequestListAsync(requestList);
    }

    @Override
    public void play(String roomUuid) {
        List<SpotifyToken> spotifyTokenList = tokenService.getTokenListByRoomUuid(roomUuid);

        ARequestBuilder request = new ARequestBuilder() {
            @Override
            public AbstractDataRequest build(SpotifyApi spotifyApi) {
                return spotifyApi.startResumeUsersPlayback().build();
            }
        };

        List<AbstractDataRequest> requestList = spotifyRequest.initRequestList(spotifyTokenList, request);
        spotifyRequest.execRequestListAsync(requestList);
    }

    @Override
    public void pause(String roomUuid) {
        List<SpotifyToken> spotifyTokenList = tokenService.getTokenListByRoomUuid(roomUuid);

        ARequestBuilder request = new ARequestBuilder() {
            @Override
            public AbstractDataRequest build(SpotifyApi spotifyApi) {
                return spotifyApi.pauseUsersPlayback().build();
            }
        };

        List<AbstractDataRequest> requestList = spotifyRequest.initRequestList(spotifyTokenList, request);
        spotifyRequest.execRequestListAsync(requestList);
    }

    @Override
    public void next(String roomUuid) {
        List<SpotifyToken> spotifyTokenList = tokenService.getTokenListByRoomUuid(roomUuid);

        ARequestBuilder request = new ARequestBuilder() {
            @Override
            public AbstractDataRequest build(SpotifyApi spotifyApi) {
                return spotifyApi.skipUsersPlaybackToNextTrack().build();
            }
        };

        List<AbstractDataRequest> requestList = spotifyRequest.initRequestList(spotifyTokenList, request);
        spotifyRequest.execRequestListAsync(requestList);
    }

    @Override
    public void previous(String roomUuid) {
        List<SpotifyToken> spotifyTokenList = tokenService.getTokenListByRoomUuid(roomUuid);

        ARequestBuilder request = new ARequestBuilder() {
            @Override
            public AbstractDataRequest build(SpotifyApi spotifyApi) {
                return spotifyApi.skipUsersPlaybackToPreviousTrack().build();
            }
        };

        List<AbstractDataRequest> requestList = spotifyRequest.initRequestList(spotifyTokenList, request);
        spotifyRequest.execRequestListAsync(requestList);
    }

    @Override
    public void seek(String roomUuid, Integer ms) {
        List<SpotifyToken> spotifyTokenList = tokenService.getTokenListByRoomUuid(roomUuid);

        ARequestBuilder request = new ARequestBuilder() {
            @Override
            public AbstractDataRequest build(SpotifyApi spotifyApi) {
                return spotifyApi.seekToPositionInCurrentlyPlayingTrack(ms).build();
            }
        };

        List<AbstractDataRequest> requestList = spotifyRequest.initRequestList(spotifyTokenList, request);
        spotifyRequest.execRequestListAsync(requestList);
    }

    @Override
    public void repeat(String roomUuid) {
        List<SpotifyToken> spotifyTokenList = tokenService.getTokenListByRoomUuid(roomUuid);

        ARequestBuilder request = new ARequestBuilder() {
            @Override
            public AbstractDataRequest build(SpotifyApi spotifyApi) {
                String state = "track";
                return spotifyApi.setRepeatModeOnUsersPlayback(state).build();
            }
        };

        List<AbstractDataRequest> requestList = spotifyRequest.initRequestList(spotifyTokenList, request);
        spotifyRequest.execRequestListAsync(requestList);
    }


    @Override
    public List<UserDevice> getUsersAvailableDevices(String userUuid) {
        List<UserDevice> userDeviceList = new LinkedList<>();
        SpotifyToken spotifyToken = null;

        try {
            Optional<SpotifyToken> spotifyTokenOpt;
            spotifyTokenOpt = tokenService.getTokenByUserUuid(userUuid);

            if(spotifyTokenOpt.isPresent())
                spotifyToken = spotifyTokenOpt.get();

        } catch (DatabaseException e) {
            e.printStackTrace();
        }

        ARequestBuilder request = new ARequestBuilder() {
            @Override
            public AbstractDataRequest build(SpotifyApi spotifyApi) {
                return spotifyApi.getUsersAvailableDevices().build();
            }
        };

        AbstractDataRequest dataRequest = spotifyRequest.initRequest(spotifyToken, request);

        try {
            Device[] devices = ((GetUsersAvailableDevicesRequest) dataRequest).execute();

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

        } catch (IOException | SpotifyWebApiException e) {
            LOGGER.error("An error occurred while getting devices.");
        }

        return userDeviceList;
    }
}
