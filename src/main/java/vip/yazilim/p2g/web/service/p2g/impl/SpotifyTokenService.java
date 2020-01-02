package vip.yazilim.p2g.web.service.p2g.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.OAuthToken;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.repository.ISpotifyTokenRepo;
import vip.yazilim.p2g.web.service.p2g.ISpotifyTokenService;
import vip.yazilim.p2g.web.service.p2g.IUserService;
import vip.yazilim.spring.core.exception.general.InvalidArgumentException;
import vip.yazilim.spring.core.exception.general.InvalidUpdateException;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;
import vip.yazilim.spring.core.exception.general.database.DatabaseReadException;
import vip.yazilim.spring.core.exception.web.NotFoundException;
import vip.yazilim.spring.core.service.ACrudServiceImpl;

import javax.transaction.Transactional;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author mustafaarifsisman - 31.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Transactional
@Service
public class SpotifyTokenService extends ACrudServiceImpl<OAuthToken, UUID> implements ISpotifyTokenService {

    @Autowired
    private ISpotifyTokenRepo tokenRepo;

    @Autowired
    private IUserService userService;

    @Override
    protected JpaRepository<OAuthToken, UUID> getRepository() {
        return tokenRepo;
    }

    @Override
    protected UUID getId(OAuthToken entity) {
        return entity.getUserUuid();
    }

    @Override
    public String getAccessTokenByUserUuid(UUID userUuid) throws DatabaseException {
        try {
            Optional<OAuthToken> spotifyToken = tokenRepo.findSpotifyTokenByUserUuid(userUuid);
            return spotifyToken.map(OAuthToken::getAccessToken).orElseThrow(() -> new NotFoundException("Token not found for userUuid: " + userUuid));
        } catch (Exception exception) {
            String errorMessage = String.format("An error occurred while getting Tokens with userUuid[%s]", userUuid);
            throw new DatabaseReadException(errorMessage, exception);
        }
    }

    @Override
    public Optional<OAuthToken> getTokenByUserUuid(UUID userUuid) throws DatabaseException {
        try {
            return tokenRepo.findSpotifyTokenByUserUuid(userUuid);
        } catch (Exception exception) {
            String errorMessage = String.format("An error occurred while getting Tokens with userUuid[%s]", userUuid);
            throw new DatabaseReadException(errorMessage, exception);
        }
    }

    @Override
    public OAuthToken saveUserToken(UUID userUuid, String accessToken, String refreshToken) throws DatabaseException, InvalidUpdateException, InvalidArgumentException {
        Optional<OAuthToken> spotifyToken = getTokenByUserUuid(userUuid);

        if (spotifyToken.isPresent()) {
            OAuthToken token = spotifyToken.get();
            token.setAccessToken(accessToken);
            token.setRefreshToken(refreshToken);
            return update(token);
        }

        OAuthToken entity = new OAuthToken();
        entity.setUserUuid(userUuid);
        entity.setAccessToken(accessToken);
        entity.setRefreshToken(refreshToken);
        return create(entity);
    }

    @Override
    public List<OAuthToken> getTokenListByroomId(Long roomId) throws DatabaseException, InvalidArgumentException {
        List<OAuthToken> OAuthTokenList = new LinkedList<>();
        List<User> userList = userService.getUsersByroomId(roomId);

        for (User u : userList) {
            Optional<OAuthToken> token = getTokenByUserUuid(u.getUuid());
            token.ifPresent(OAuthTokenList::add);
        }

        return OAuthTokenList;
    }

}
