package vip.yazilim.p2g.web.service.spotify;


import com.wrapper.spotify.model_objects.specification.User;
import vip.yazilim.p2g.web.exception.RequestException;
import vip.yazilim.p2g.web.exception.TokenException;
import vip.yazilim.spring.utils.exception.DatabaseException;

/**
 * @author mustafaarifsisman - 26.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ISpotifyProfileService {

    User getSpotifyUser(String spotifyAccountId) throws RequestException;

    User getCurrentSpotifyUser(String userUuid) throws DatabaseException, TokenException, RequestException;

}
