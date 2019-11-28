package vip.yazilim.p2g.web.service;

import vip.yazilim.p2g.web.entity.SpotifyToken;
import vip.yazilim.spring.utils.exception.DatabaseException;
import vip.yazilim.spring.utils.exception.InvalidUpdateException;
import vip.yazilim.spring.utils.service.ICrudService;

import javax.xml.crypto.Data;
import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ITokenService extends ICrudService<SpotifyToken, String> {

    Optional<SpotifyToken> getTokenByUserUuid(String userUuid) throws DatabaseException;

    SpotifyToken saveUserToken(String userUuid, String accessToken, String refreshToken) throws DatabaseException, InvalidUpdateException;

    List<SpotifyToken> getTokenListByRoomUuid(String roomUuid);

}