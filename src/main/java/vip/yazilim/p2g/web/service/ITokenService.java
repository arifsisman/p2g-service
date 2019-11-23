package vip.yazilim.p2g.web.service;

import vip.yazilim.p2g.web.entity.Token;
import vip.yazilim.spring.utils.exception.DatabaseException;
import vip.yazilim.spring.utils.exception.InvalidUpdateException;
import vip.yazilim.spring.utils.service.ICrudService;

import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ITokenService extends ICrudService<Token, String> {

    Optional<Token> getTokenByUserUuid(String userUuid) throws DatabaseException;

    Token saveUserToken(String userUuid, String accessToken, String refreshToken) throws DatabaseException, InvalidUpdateException;

}