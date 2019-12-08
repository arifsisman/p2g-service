package vip.yazilim.p2g.web.service.p2g;

import vip.yazilim.p2g.web.entity.relation.SpotifyToken;
import vip.yazilim.spring.core.exception.general.InvalidArgumentException;
import vip.yazilim.spring.core.exception.general.InvalidUpdateException;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;
import vip.yazilim.spring.core.service.ICrudService;

import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ITokenService extends ICrudService<SpotifyToken, String> {

    String getAccessTokenByUserUuid(String userUuid) throws DatabaseException;
    Optional<SpotifyToken> getTokenByUserUuid(String userUuid) throws DatabaseException;

    SpotifyToken saveUserToken(String userUuid, String accessToken, String refreshToken) throws DatabaseException, InvalidUpdateException, InvalidArgumentException;

    List<SpotifyToken> getTokenListByRoomUuid(String roomUuid) throws DatabaseException, InvalidArgumentException;

}