package vip.yazilim.p2g.web.service.p2g.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.libs.springcore.exception.DatabaseReadException;
import vip.yazilim.libs.springcore.service.ACrudServiceImpl;
import vip.yazilim.p2g.web.entity.OAuthToken;
import vip.yazilim.p2g.web.repository.ISpotifyTokenRepo;
import vip.yazilim.p2g.web.rest.spotify.SpotifyAuthorizationRest;
import vip.yazilim.p2g.web.service.p2g.ISpotifyTokenService;
import vip.yazilim.p2g.web.service.p2g.IUserService;

import java.util.Optional;

/**
 * @author mustafaarifsisman - 31.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class SpotifyTokenService extends ACrudServiceImpl<OAuthToken, String> implements ISpotifyTokenService {

    @Autowired
    private ISpotifyTokenRepo tokenRepo;

    @Autowired
    private IUserService userService;

    @Autowired
    private SpotifyAuthorizationRest spotifyAuthorizationRest;

    @Override
    protected JpaRepository<OAuthToken, String> getRepository() {
        return tokenRepo;
    }

    @Override
    protected String getId(OAuthToken entity) {
        return entity.getUserId();
    }

    @Override
    protected Class<OAuthToken> getClassOfEntity() {
        return OAuthToken.class;
    }

    @Override
    public Optional<OAuthToken> getTokenByUserId(String userId) {
        try {
            return tokenRepo.findOAuthTokenByUserId(userId);
        } catch (Exception exception) {
            throw new DatabaseReadException(getClassOfEntity(), exception, userId);
        }
    }

    @Override
    public String saveUserToken(String userId, String accessToken) {
        Optional<OAuthToken> spotifyToken = getTokenByUserId(userId);

        if (spotifyToken.isPresent()) {
            OAuthToken token = spotifyToken.get();
            token.setAccessToken(accessToken);
            return update(token).getAccessToken();
        }

        OAuthToken entity = new OAuthToken();
        entity.setUserId(userId);
        entity.setAccessToken(accessToken);
        return create(entity).getAccessToken();
    }
}
