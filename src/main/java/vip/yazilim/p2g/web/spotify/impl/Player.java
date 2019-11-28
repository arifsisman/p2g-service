package vip.yazilim.p2g.web.spotify.impl;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.miscellaneous.Device;
import com.wrapper.spotify.requests.AbstractRequest;
import com.wrapper.spotify.requests.data.AbstractDataRequest;
import com.wrapper.spotify.requests.data.player.GetUsersAvailableDevicesRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.SpotifyToken;
import vip.yazilim.p2g.web.service.ITokenService;
import vip.yazilim.p2g.web.spotify.IPlayer;
import vip.yazilim.p2g.web.spotify.Request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class Player implements IPlayer {

    private final Logger LOGGER = LoggerFactory.getLogger(Player.class);

    @Autowired
    private ITokenService tokenService;

    @Override
    public List<AbstractDataRequest> initRequests(List<SpotifyToken> spotifyTokenList, Request request) {

        List<AbstractDataRequest> requestList = new ArrayList<>();
        SpotifyApi spotifyApi;

        for (SpotifyToken token : spotifyTokenList) {
            spotifyApi = new SpotifyApi.Builder()
                    .setAccessToken(token.getAccessToken())
                    .build();

            requestList.add(request.build(spotifyApi));
        }

        return requestList;
    }

    @Override
    public void execRequestsAsync(List<AbstractDataRequest> requestList) {
        requestList.forEach(AbstractRequest::executeAsync);
    }

    @Override
    public void execRequestsSync(List<AbstractDataRequest> requestList) {
        for (AbstractDataRequest dataRequest : requestList) {
            try {
                dataRequest.execute();
            } catch (IOException | SpotifyWebApiException e) {
                LOGGER.error("An error occurred while executing request.");
            }
        }
    }

    @Override
    public void play(String roomUuid, String songUri) {
        List<SpotifyToken> spotifyTokenList = tokenService.getTokenListByRoomUuid(roomUuid);

        Request request = new Request() {
            @Override
            public AbstractDataRequest build(SpotifyApi spotifyApi) {
                return spotifyApi.startResumeUsersPlayback().context_uri(songUri).build();
            }
        };

        List<AbstractDataRequest> requestList = initRequests(spotifyTokenList, request);
        execRequestsAsync(requestList);
    }

    @Override
    public void play(String roomUuid) {
        List<SpotifyToken> spotifyTokenList = tokenService.getTokenListByRoomUuid(roomUuid);

        Request request = new Request() {
            @Override
            public AbstractDataRequest build(SpotifyApi spotifyApi) {
                return spotifyApi.startResumeUsersPlayback().build();
            }
        };

        List<AbstractDataRequest> requestList = initRequests(spotifyTokenList, request);
        execRequestsAsync(requestList);
    }

    @Override
    public void pause(String roomUuid) {
        List<SpotifyToken> spotifyTokenList = tokenService.getTokenListByRoomUuid(roomUuid);

        Request request = new Request() {
            @Override
            public AbstractDataRequest build(SpotifyApi spotifyApi) {
                return spotifyApi.pauseUsersPlayback().build();
            }
        };

        List<AbstractDataRequest> requestList = initRequests(spotifyTokenList, request);
        execRequestsAsync(requestList);
    }

    @Override
    public void next(String roomUuid) {
        List<SpotifyToken> spotifyTokenList = tokenService.getTokenListByRoomUuid(roomUuid);

        Request request = new Request() {
            @Override
            public AbstractDataRequest build(SpotifyApi spotifyApi) {
                return spotifyApi.skipUsersPlaybackToNextTrack().build();
            }
        };

        List<AbstractDataRequest> requestList = initRequests(spotifyTokenList, request);
        execRequestsAsync(requestList);
    }

    @Override
    public void previous(String roomUuid) {
        List<SpotifyToken> spotifyTokenList = tokenService.getTokenListByRoomUuid(roomUuid);

        Request request = new Request() {
            @Override
            public AbstractDataRequest build(SpotifyApi spotifyApi) {
                return spotifyApi.skipUsersPlaybackToPreviousTrack().build();
            }
        };

        List<AbstractDataRequest> requestList = initRequests(spotifyTokenList, request);
        execRequestsAsync(requestList);
    }

    @Override
    public void seek(String roomUuid, Integer ms) {
        List<SpotifyToken> spotifyTokenList = tokenService.getTokenListByRoomUuid(roomUuid);

        Request request = new Request() {
            @Override
            public AbstractDataRequest build(SpotifyApi spotifyApi) {
                return spotifyApi.seekToPositionInCurrentlyPlayingTrack(ms).build();
            }
        };

        List<AbstractDataRequest> requestList = initRequests(spotifyTokenList, request);
        execRequestsAsync(requestList);
    }

    @Override
    public void repeat(String roomUuid) {
        List<SpotifyToken> spotifyTokenList = tokenService.getTokenListByRoomUuid(roomUuid);

        Request request = new Request() {
            @Override
            public AbstractDataRequest build(SpotifyApi spotifyApi) {
                String state = "track";
                return spotifyApi.setRepeatModeOnUsersPlayback(state).build();
            }
        };

        List<AbstractDataRequest> requestList = initRequests(spotifyTokenList, request);
        execRequestsAsync(requestList);
    }


    @Override
    public List<String> getUsersAvailableDevices(SpotifyToken token) {
        List<SpotifyToken> spotifyTokenList = new ArrayList<>();
        List<String> deviceIdList = new ArrayList<>();

        spotifyTokenList.add(token);

        Request request = new Request() {
            @Override
            public AbstractDataRequest build(SpotifyApi spotifyApi) {
                return spotifyApi.getUsersAvailableDevices().build();
            }
        };

        List<AbstractDataRequest> requestList = initRequests(spotifyTokenList, request);

        try {
            Device[] devices = ((GetUsersAvailableDevicesRequest) requestList.get(0)).execute();

            for(Device d:devices){
                deviceIdList.add(d.getId());
            }

        } catch (IOException | SpotifyWebApiException e) {
            LOGGER.error("An error occurred while getting devices.");
        }

        return deviceIdList;
    }
}
