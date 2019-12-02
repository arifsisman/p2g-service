package vip.yazilim.p2g.web.service.p2g.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.SpotifyToken;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.repository.ITokenRepo;
import vip.yazilim.p2g.web.service.p2g.ITokenService;
import vip.yazilim.p2g.web.service.p2g.IUserService;
import vip.yazilim.p2g.web.util.DBHelper;
import vip.yazilim.spring.utils.exception.DatabaseException;
import vip.yazilim.spring.utils.exception.InvalidUpdateException;
import vip.yazilim.spring.utils.service.ACrudServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 31.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class TokenService extends ACrudServiceImpl<SpotifyToken, String> implements ITokenService {

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
    public Optional<SpotifyToken> getTokenByUserUuid(String userUuid) throws DatabaseException {
        try {
            return tokenRepo.findSpotifyTokenByUserUuid(userUuid);
        } catch (Exception exception) {
            String errorMessage = String.format("An error occurred while getting Tokens with userUuid[%s]", userUuid);
            throw new DatabaseException(errorMessage, exception);
        }
    }

    @Override
    public SpotifyToken saveUserToken(String userUuid, String accessToken, String refreshToken) throws DatabaseException, InvalidUpdateException {

        Optional<SpotifyToken> token = getTokenByUserUuid(userUuid);

        if (token.isPresent()) {
            LOGGER.debug("Updating token for userUuid:" + userUuid);
            token.get().setAccessToken(accessToken);
            token.get().setRefreshToken(refreshToken);
            return update(token.get());
        }

        SpotifyToken entity = new SpotifyToken();
        entity.setUserUuid(userUuid);
        entity.setAccessToken(accessToken);
        entity.setRefreshToken(refreshToken);
        return create(entity);
    }

    @Override
    public List<SpotifyToken> getTokenListByRoomUuid(String roomUuid) throws DatabaseException {
        List<User> userList;
        List<SpotifyToken> spotifyTokenList = new ArrayList<>();

        userList = userService.getUsersByRoomUuid(roomUuid);

        try {
            for (User u : userList) {
                Optional<SpotifyToken> token = getTokenByUserUuid(u.getUuid());
                token.ifPresent(spotifyTokenList::add);
            }
        } catch (DatabaseException e) {
            throw new DatabaseException("An error occurred while getting tokenList from roomUuid:" + roomUuid, e);
        }

        return spotifyTokenList;
    }

}
