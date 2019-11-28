package vip.yazilim.p2g.web.spotify.impl;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.requests.data.AbstractDataRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.SpotifyToken;
import vip.yazilim.p2g.web.service.ITokenService;
import vip.yazilim.p2g.web.spotify.IPlayer;

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
    public List<AbstractDataRequest> initRequests(String roomUuid, Request request) {

        List<AbstractDataRequest> requestList = new ArrayList<>();
        List<SpotifyToken> spotifyTokenList = tokenService.getTokenListByRoomUuid(roomUuid);

        for (SpotifyToken token : spotifyTokenList) {
            SpotifyApi spotifyApi = new SpotifyApi.Builder()
                    .setAccessToken(token.getAccessToken())
                    .build();

            requestList.add(request.build(spotifyApi));
        }

        return requestList;
    }

    @Override
    public void execRequests(List<AbstractDataRequest> requestList) {
        for (AbstractDataRequest request : requestList) {
            request.executeAsync();
        }
    }

    @Override
    public void play(String roomUuid, String songUri) {
//        List<AbstractDataRequest> requestList = new ArrayList<>();
//        List<SpotifyToken> spotifyTokenList = tokenService.getTokenListByRoomUuid(roomUuid);
//
//        for (SpotifyToken token : spotifyTokenList) {
//            String accessToken = token.getAccessToken();
//
//            SpotifyApi spotifyApi = new SpotifyApi.Builder()
//                    .setAccessToken(accessToken)
//                    .build();
//
//            StartResumeUsersPlaybackRequest request = spotifyApi.startResumeUsersPlayback()
//                    .context_uri(songUri)
//                    .build();
//
//            requestList.add(request);
//        }

        Request request = new Request() {
            @Override
            public AbstractDataRequest build(SpotifyApi spotifyApi) {
                return spotifyApi.startResumeUsersPlayback()
                        .context_uri(songUri)
                        .build();
            }
        };

        List<AbstractDataRequest> requestList = initRequests(roomUuid, request);
        execRequests(requestList);
    }

    @Override
    public void play(String roomUuid) {

    }

    @Override
    public void pause(String roomUuid) {

    }

    @Override
    public void next(String roomUuid) {

    }

    @Override
    public void previous(String roomUuid) {

    }

    @Override
    public void seek(String roomUuid, Integer ms) {

    }

    @Override
    public void repeat(String roomUuid) {

    }


    @Override
    public List<String> getUsersAvailableDevices() {
        List<String> deviceIdList = new ArrayList<>();
//        GetUsersAvailableDevicesRequest request = spotifyApi
//                .getUsersAvailableDevices()
//                .build();
//
//        try {
//            Device[] devices = request.execute();
//
//            for (Device d : devices) {
//                deviceIdList.add(d.getId());
//            }
//        } catch (IOException | SpotifyWebApiException e) {
//            LOGGER.error("An error occurred while getting devices.");
//        }
//
        return deviceIdList;
    }
}
