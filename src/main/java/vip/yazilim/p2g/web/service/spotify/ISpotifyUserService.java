package vip.yazilim.p2g.web.service.spotify;

import com.wrapper.spotify.model_objects.specification.User;
import vip.yazilim.p2g.web.entity.UserDevice;

import java.util.List;

/**
 * @author mustafaarifsisman - 26.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ISpotifyUserService {
    User getSpotifyUser(String spotifyAccountId);

    User getCurrentSpotifyUser(String userId);

    List<UserDevice> getUsersAvailableDevices(String userId);

    boolean transferUsersPlayback(UserDevice userDevice);

}
