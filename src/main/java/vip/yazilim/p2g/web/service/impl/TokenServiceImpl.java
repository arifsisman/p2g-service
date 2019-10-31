package vip.yazilim.p2g.web.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.Token;
import vip.yazilim.p2g.web.entity.relation.UserToken;
import vip.yazilim.p2g.web.exception.DatabaseException;
import vip.yazilim.p2g.web.repository.ITokenRepo;
import vip.yazilim.p2g.web.repository.relation.IUserTokenRepo;
import vip.yazilim.p2g.web.service.ITokenService;

import java.util.ArrayList;
import java.util.List;

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
        List<Token> tokenList;
        Iterable<UserToken> userTokenIterable;

        try {
            tokenList = new ArrayList<>();

            userTokenIterable = userTokenRepo.findTokensByUserUuid(userUuid);

            for (UserToken token : userTokenIterable) {
                tokenList.add(tokenRepo.findByUuid(token.getTokenUuid()).get());
            }

        }catch (Exception exception){
            String errorMessage = String.format("An error occurred while getting Tokens with userUuid[%s]", userUuid);
            throw new DatabaseException(errorMessage, exception);
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
