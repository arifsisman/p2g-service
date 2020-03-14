package vip.yazilim.p2g.web.service.spotify;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.User;
import vip.yazilim.libs.springcore.exception.general.database.DatabaseException;
import vip.yazilim.p2g.web.entity.UserDevice;

import java.io.IOException;
import java.util.List;

/**
 * @author mustafaarifsisman - 26.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ISpotifyUserService {

    User getSpotifyUser(String spotifyAccountId) throws IOException, SpotifyWebApiException, DatabaseException;
    User getCurrentSpotifyUser(String userId) throws DatabaseException, IOException, SpotifyWebApiException;

    List<UserDevice> getUsersAvailableDevices(String userId) throws DatabaseException, IOException, SpotifyWebApiException;
    boolean transferUsersPlayback(UserDevice userDevice) throws DatabaseException, IOException, SpotifyWebApiException;

}
