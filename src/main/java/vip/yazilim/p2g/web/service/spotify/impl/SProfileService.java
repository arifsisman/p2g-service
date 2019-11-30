package vip.yazilim.p2g.web.service.spotify.impl;

import com.wrapper.spotify.model_objects.specification.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.SpotifyToken;
import vip.yazilim.p2g.web.exception.TokenException;
import vip.yazilim.p2g.web.service.p2g.ITokenService;
import vip.yazilim.p2g.web.service.spotify.ISProfileService;
import vip.yazilim.p2g.web.service.spotify.ISRequestService;
import vip.yazilim.spring.utils.exception.DatabaseException;

/**
 * @author mustafaarifsisman - 26.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class SProfileService implements ISProfileService {

    @Autowired
    private ISRequestService spotifyRequest;

    @Autowired
    private ITokenService tokenService;

    @Override
    public User getSpotifyUser(String spotifyAccountId) {
        return spotifyRequest.execRequestSync((spotifyApi) -> spotifyApi.getUsersProfile(spotifyAccountId).build());
    }

    @Override
    public User getCurrentSpotifyUser(String userUuid) throws DatabaseException, TokenException {
        SpotifyToken spotifyToken = tokenService.getTokenByUserUuid(userUuid).orElseThrow(() -> new TokenException("Token not found!"));
        return spotifyRequest.execRequestSync((spotifyApi) -> spotifyApi.getCurrentUsersProfile().build(), spotifyToken);
    }

}