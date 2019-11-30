package vip.yazilim.p2g.web.service.spotify;


import com.wrapper.spotify.model_objects.specification.User;
import vip.yazilim.p2g.web.exception.TokenException;
import vip.yazilim.spring.utils.exception.DatabaseException;

/**
 * @author mustafaarifsisman - 26.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ISProfileService {

    User getSpotifyUser(String spotifyAccountId);

    User getCurrentSpotifyUser(String userUuid) throws DatabaseException, TokenException;

}
