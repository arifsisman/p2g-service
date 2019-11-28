package vip.yazilim.p2g.web.spotify.impl;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.requests.data.AbstractDataRequest;
import com.wrapper.spotify.requests.data.player.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import vip.yazilim.p2g.web.constant.Constants;
import vip.yazilim.p2g.web.entity.SpotifyToken;
import vip.yazilim.p2g.web.exception.PlayerException;
import vip.yazilim.p2g.web.service.ITokenService;
import vip.yazilim.p2g.web.spotify.IPlayer;

import java.util.List;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Controller
public class Player implements IPlayer {

    private final Logger LOGGER = LoggerFactory.getLogger(Player.class);

    @Autowired
    @Qualifier(Constants.BEAN_NAME_AUTHORIZATION_CODE)
    private SpotifyApi spotifyApi;

    @Autowired
    private ITokenService tokenService;

    @Override
    public boolean executeRequest(String roomUuid, AbstractDataRequest request) {
        List<SpotifyToken> spotifyTokenList;

        spotifyTokenList = tokenService.getTokenListByRoomUuid(roomUuid);

        try {
            for (SpotifyToken token : spotifyTokenList) {
                String accessToken = token.getAccessToken();
                spotifyApi.setAccessToken(accessToken);
                request.executeAsync();
            }
            return true;
        } catch (Exception e) {
            LOGGER.error("An error occurred while executing request[{}], roomUuid[{}]"
                    , request.getClass().getName(), roomUuid);
            throw new PlayerException("An error occurred while executing request", e);
        }
    }

    @Override
    public boolean play(String roomUuid, String songUri) {
        StartResumeUsersPlaybackRequest request = spotifyApi.startResumeUsersPlayback()
                .context_uri(songUri)
                .build();

        return executeRequest(roomUuid, request);
    }

    @Override
    public boolean play(String roomUuid) {
        StartResumeUsersPlaybackRequest request = spotifyApi.startResumeUsersPlayback()
                .build();

        return executeRequest(roomUuid, request);
    }

    @Override
    public boolean pause(String roomUuid) {
        PauseUsersPlaybackRequest request = spotifyApi.pauseUsersPlayback()
                .build();

        return executeRequest(roomUuid, request);
    }

    @Override
    public boolean next(String roomUuid) {
        SkipUsersPlaybackToNextTrackRequest request = spotifyApi.skipUsersPlaybackToNextTrack()
                .build();

        return executeRequest(roomUuid, request);
    }

    @Override
    public boolean previous(String roomUuid) {
        SkipUsersPlaybackToPreviousTrackRequest request = spotifyApi.skipUsersPlaybackToPreviousTrack()
                .build();

        return executeRequest(roomUuid, request);
    }

    @Override
    public boolean seek(String roomUuid, Integer positionMs) {
        SeekToPositionInCurrentlyPlayingTrackRequest request =
                spotifyApi.seekToPositionInCurrentlyPlayingTrack(positionMs).build();

        return executeRequest(roomUuid, request);
    }

    @Override
    public boolean repeat(String roomUuid) {
        String state = "track";

        SetRepeatModeOnUsersPlaybackRequest request = spotifyApi.setRepeatModeOnUsersPlayback(state)
                .build();

        return executeRequest(roomUuid, request);
    }
}
