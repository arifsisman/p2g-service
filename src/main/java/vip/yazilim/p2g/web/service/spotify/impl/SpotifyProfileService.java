package vip.yazilim.p2g.web.service.spotify.impl;

import com.wrapper.spotify.model_objects.specification.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.SpotifyToken;
import vip.yazilim.p2g.web.exception.RequestException;
import vip.yazilim.p2g.web.exception.TokenException;
import vip.yazilim.p2g.web.service.p2g.ITokenService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyProfileService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyRequestService;
import vip.yazilim.spring.utils.exception.DatabaseException;

/**
 * @author mustafaarifsisman - 26.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class SpotifyProfileService implements ISpotifyProfileService {

    @Autowired
    private ISpotifyRequestService spotifyRequest;

    @Autowired
    private ITokenService tokenService;

    @Override
    public User getSpotifyUser(String spotifyAccountId) throws RequestException {
        return spotifyRequest.execRequestSync((spotifyApi) -> spotifyApi.getUsersProfile(spotifyAccountId).build());
    }

    @Override
    public User getCurrentSpotifyUser(String userUuid) throws DatabaseException, TokenException, RequestException {
        SpotifyToken spotifyToken = tokenService.getTokenByUserUuid(userUuid).orElseThrow(() -> new TokenException("Token not found!"));
        return spotifyRequest.execRequestSync((spotifyApi) -> spotifyApi.getCurrentUsersProfile().build(), spotifyToken);
    }

}