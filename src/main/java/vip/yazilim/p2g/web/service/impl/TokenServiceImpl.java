package vip.yazilim.p2g.web.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.Token;
import vip.yazilim.p2g.web.repository.ITokenRepo;
import vip.yazilim.p2g.web.service.ITokenService;
import vip.yazilim.p2g.web.util.DBHelper;
import vip.yazilim.spring.utils.exception.DatabaseException;
import vip.yazilim.spring.utils.exception.InvalidUpdateException;
import vip.yazilim.spring.utils.service.ACrudServiceImpl;

import java.util.Optional;

/**
 * @author mustafaarifsisman - 31.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class TokenServiceImpl extends ACrudServiceImpl<Token, String> implements ITokenService {

    private Logger LOGGER = LoggerFactory.getLogger(TokenServiceImpl.class);

    @Autowired
    private ITokenRepo tokenRepo;

    @Override
    public Optional<Token> getTokenByUserUuid(String userUuid) throws DatabaseException {
        try {
            return tokenRepo.findTokenByUserUuid(userUuid);
        } catch (Exception exception) {
            String errorMessage = String.format("An error occurred while getting Tokens with userUuid[%s]", userUuid);
            throw new DatabaseException(errorMessage, exception);
        }
    }

    @Override
    public Token saveUserToken(String userUuid, String accessToken, String refreshToken) throws DatabaseException, InvalidUpdateException {

        Optional<Token> token = getTokenByUserUuid(userUuid);

        if(token.isPresent()) {
            LOGGER.debug("Updating token for userUuid:"+ userUuid);
            token.get().setAccessToken(accessToken);
            token.get().setRefreshToken(refreshToken);
            return update(token.get());
        }

        Token entity = new Token();
        entity.setUserUuid(userUuid);
        entity.setAccessToken(accessToken);
        entity.setRefreshToken(refreshToken);
        return create(entity);
    }

    @Override
    protected JpaRepository<Token, String> getRepository() {
        return tokenRepo;
    }

    @Override
    protected String getId(Token entity) {
        return entity.getUuid();
    }

    @Override
    protected Token preInsert(Token entity) {
        entity.setUuid(DBHelper.getRandomUuid());
        return entity;
    }
}
