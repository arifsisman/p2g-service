package vip.yazilim.p2g.web.service.p2g.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.entity.relation.SpotifyToken;
import vip.yazilim.p2g.web.repository.ITokenRepo;
import vip.yazilim.p2g.web.service.p2g.ITokenService;
import vip.yazilim.p2g.web.service.p2g.IUserService;
import vip.yazilim.p2g.web.util.DBHelper;
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

/**
 * @author mustafaarifsisman - 31.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Transactional
@Service
public class  TokenService extends ACrudServiceImpl<SpotifyToken, String> implements ITokenService {

    private Logger LOGGER = LoggerFactory.getLogger(TokenService.class);

    @Autowired
    private ITokenRepo tokenRepo;

    @Autowired
    private IUserService userService;

    @Override
    protected JpaRepository<SpotifyToken, String> getRepository() {
        return tokenRepo;
    }

    @Override
    protected String getId(SpotifyToken entity) {
        return entity.getUuid();
    }

    @Override
    protected SpotifyToken preInsert(SpotifyToken entity) {
        entity.setUuid(DBHelper.getRandomUuid());
        return entity;
    }

    @Override
    public String getAccessTokenByUserUuid(String userUuid) throws DatabaseException {
        try {
            Optional<SpotifyToken> spotifyToken = tokenRepo.findSpotifyTokenByUserUuid(userUuid);
            return spotifyToken.map(SpotifyToken::getAccessToken).orElseThrow(() -> new NotFoundException("Token not found for userUuid: " + userUuid));
        } catch (Exception exception) {
            String errorMessage = String.format("An error occurred while getting Tokens with userUuid[%s]", userUuid);
            throw new DatabaseReadException(errorMessage, exception);
        }
    }

    @Override
    public Optional<SpotifyToken> getTokenByUserUuid(String userUuid) throws DatabaseException {
        try {
            return tokenRepo.findSpotifyTokenByUserUuid(userUuid);
        } catch (Exception exception) {
            String errorMessage = String.format("An error occurred while getting Tokens with userUuid[%s]", userUuid);
            throw new DatabaseReadException(errorMessage, exception);
        }
    }

    @Override
    public SpotifyToken saveUserToken(String userUuid, String accessToken, String refreshToken) throws DatabaseException, InvalidUpdateException, InvalidArgumentException {
        Optional<SpotifyToken> spotifyToken = getTokenByUserUuid(userUuid);

        if (spotifyToken.isPresent()) {
            SpotifyToken token = spotifyToken.get();
            LOGGER.debug("Updating token for userUuid:" + userUuid);
            token.setAccessToken(accessToken);
            token.setRefreshToken(refreshToken);
            return update(token);
        }

        SpotifyToken entity = new SpotifyToken();
        entity.setUserUuid(userUuid);
        entity.setAccessToken(accessToken);
        entity.setRefreshToken(refreshToken);
        return create(entity);
    }

    @Override
    public List<SpotifyToken> getTokenListByRoomUuid(String roomUuid) throws DatabaseException, InvalidArgumentException {
        List<SpotifyToken> spotifyTokenList = new LinkedList<>();
        List<User> userList = userService.getUsersByRoomUuid(roomUuid);

        for (User u : userList) {
            Optional<SpotifyToken> token = getTokenByUserUuid(u.getUuid());
            token.ifPresent(spotifyTokenList::add);
        }

        return spotifyTokenList;
    }

}
