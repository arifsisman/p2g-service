package vip.yazilim.p2g.web.service;

import vip.yazilim.p2g.web.entity.Token;
import vip.yazilim.p2g.web.exception.DatabaseException;

import java.util.List;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ITokenService extends ICrudService<Token, String> {

    List<Token> getTokensByUserUuid(String userUuid) throws DatabaseException;

}
