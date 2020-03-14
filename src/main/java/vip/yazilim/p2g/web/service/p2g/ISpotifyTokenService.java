package vip.yazilim.p2g.web.service.p2g;

import vip.yazilim.libs.springcore.exception.general.BusinessLogicException;
import vip.yazilim.libs.springcore.exception.general.InvalidArgumentException;
import vip.yazilim.libs.springcore.exception.general.database.DatabaseException;
import vip.yazilim.libs.springcore.service.ICrudService;
import vip.yazilim.p2g.web.entity.OAuthToken;

import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ISpotifyTokenService extends ICrudService<OAuthToken, String> {

    String getAccessTokenByUserId(String userId) throws DatabaseException;

    Optional<OAuthToken> getTokenByUserId(String userId) throws DatabaseException;

    OAuthToken saveUserToken(String userId, String accessToken, String refreshToken) throws BusinessLogicException;

    String saveUserToken(String userId, String accessToken) throws BusinessLogicException;

    List<OAuthToken> getTokenListByRoomId(Long roomId) throws DatabaseException, InvalidArgumentException;

}