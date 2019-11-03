package vip.yazilim.p2g.web.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.Token;
import vip.yazilim.p2g.web.entity.relation.UserToken;
import vip.yazilim.p2g.web.repository.ITokenRepo;
import vip.yazilim.p2g.web.repository.relation.IUserTokenRepo;
import vip.yazilim.p2g.web.service.ITokenService;
import vip.yazilim.spring.utils.exception.DatabaseException;
import vip.yazilim.spring.utils.service.ACrudServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 31.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class TokenServiceImpl extends ACrudServiceImpl<Token, String> implements ITokenService {

    @Autowired
    private ITokenRepo tokenRepo;

    @Autowired
    private IUserTokenRepo userTokenRepo;

    @Override
    public List<Token> getTokensByUserUuid(String userUuid) throws DatabaseException {

        List<UserToken> userTokenList;

        try {
            userTokenList = userTokenRepo.findTokensByUserUuid(userUuid);
        } catch (Exception exception) {
            String errorMessage = String.format("An error occurred while getting Tokens with userUuid[%s]", userUuid);
            throw new DatabaseException(errorMessage, exception);
        }

        List<Token> tokenList = new ArrayList<>();


        for (UserToken userToken : userTokenList) {
            String tokenUuid = userToken.getTokenUuid();
            Optional<Token> token = getById(tokenUuid);

            if (!token.isPresent()) {
                //TODO: new token should be requested from Spotify
                continue;
            }

            tokenList.add(token.get());
        }

        return tokenList;
    }

    @Override
    protected JpaRepository<Token, String> getRepository() {
        return tokenRepo;
    }

    @Override
    protected String getId(Token entity) {
        return entity.getUuid();
    }
}
