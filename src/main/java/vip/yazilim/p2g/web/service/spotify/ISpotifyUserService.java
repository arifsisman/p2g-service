package vip.yazilim.p2g.web.service.spotify;

import com.wrapper.spotify.model_objects.specification.User;
import vip.yazilim.p2g.web.entity.UserDevice;

import java.util.List;

/**
 * @author mustafaarifsisman - 26.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ISpotifyUserService {
    User getCurrentSpotifyUser();

    User getSpotifyUser(String spotifyAccountId);

    List<UserDevice> getUsersAvailableDevices(String userId);

    void transferUsersPlayback(UserDevice userDevice);

    vip.yazilim.p2g.web.entity.User login();

    boolean logout();

    vip.yazilim.p2g.web.entity.User saveUser(User spotifyUser, vip.yazilim.p2g.web.entity.User user);
}
