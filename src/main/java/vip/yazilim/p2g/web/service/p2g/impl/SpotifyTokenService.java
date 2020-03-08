package vip.yazilim.p2g.web.service.p2g.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.yazilim.p2g.web.entity.OAuthToken;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.repository.ISpotifyTokenRepo;
import vip.yazilim.p2g.web.service.p2g.ISpotifyTokenService;
import vip.yazilim.p2g.web.service.p2g.IUserService;
import vip.yazilim.p2g.web.util.SecurityHelper;
import vip.yazilim.spring.core.exception.GeneralException;
import vip.yazilim.spring.core.exception.InvalidArgumentException;
import vip.yazilim.spring.core.exception.database.DatabaseException;
import vip.yazilim.spring.core.exception.database.DatabaseReadException;
import vip.yazilim.spring.core.service.ACrudServiceImpl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 31.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Transactional
@Service
public class SpotifyTokenService extends ACrudServiceImpl<OAuthToken, String> implements ISpotifyTokenService {

    @Autowired
    private ISpotifyTokenRepo tokenRepo;

    @Autowired
    private IUserService userService;

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
    public String getAccessTokenByUserId(String userId) throws DatabaseException {
        try {
            Optional<OAuthToken> spotifyToken = tokenRepo.findOAuthTokenByUserId(userId);
            if (spotifyToken.isPresent()) {
                return spotifyToken.get().getAccessToken();
            } else {
                String newAccessToken = SecurityHelper.getUserAccessToken();
                OAuthToken oAuthToken = new OAuthToken();
                oAuthToken.setUserId(userId);
                oAuthToken.setAccessToken(newAccessToken);
                return create(oAuthToken).getAccessToken();
            }
        } catch (Exception exception) {
            throw new DatabaseReadException(getClassOfEntity(), exception, userId);
        }
    }

    @Override
    public Optional<OAuthToken> getTokenByUserId(String userId) throws DatabaseException {
        try {
            return tokenRepo.findOAuthTokenByUserId(userId);
        } catch (Exception exception) {
            throw new DatabaseReadException(getClassOfEntity(), exception, userId);
        }
    }

    @Override
    public OAuthToken saveUserToken(String userId, String accessToken, String refreshToken) throws GeneralException {
        Optional<OAuthToken> spotifyToken = getTokenByUserId(userId);

        if (spotifyToken.isPresent()) {
            OAuthToken token = spotifyToken.get();
            token.setAccessToken(accessToken);
            return update(token);
        }

        OAuthToken entity = new OAuthToken();
        entity.setUserId(userId);
        entity.setAccessToken(accessToken);
        return create(entity);
    }

    @Override
    public String saveUserToken(String userId, String accessToken) throws GeneralException {
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

    @Override
    public List<OAuthToken> getTokenListByRoomId(Long roomId) throws DatabaseException, InvalidArgumentException {
        List<OAuthToken> OAuthTokenList = new LinkedList<>();
        List<User> userList = userService.getUsersByRoomId(roomId);

        for (User u : userList) {
            Optional<OAuthToken> token = getTokenByUserId(u.getId());
            token.ifPresent(OAuthTokenList::add);
        }

        return OAuthTokenList;
    }

}
