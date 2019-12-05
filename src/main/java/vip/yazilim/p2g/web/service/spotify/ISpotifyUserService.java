package vip.yazilim.p2g.web.service.spotify;


import com.wrapper.spotify.model_objects.specification.User;
import vip.yazilim.p2g.web.entity.relation.UserDevice;
import vip.yazilim.p2g.web.exception.RequestException;
import vip.yazilim.p2g.web.exception.TokenException;
import vip.yazilim.spring.utils.exception.DatabaseException;

import java.util.List;

/**
 * @author mustafaarifsisman - 26.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ISpotifyUserService {

    User getSpotifyUser(String spotifyAccountId) throws RequestException;
    User getCurrentSpotifyUser(String userUuid) throws DatabaseException, TokenException, RequestException;

    List<UserDevice> getUsersAvailableDevices(String userUuid) throws DatabaseException, TokenException, RequestException;
    List<UserDevice> updateUsersAvailableDevices(String userUuid) throws DatabaseException, TokenException, RequestException;
}
