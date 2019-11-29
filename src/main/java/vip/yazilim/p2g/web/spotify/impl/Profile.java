package vip.yazilim.p2g.web.spotify.impl;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.User;
import com.wrapper.spotify.requests.data.users_profile.GetUsersProfileRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.constant.Constants;
import vip.yazilim.p2g.web.spotify.IProfile;
import vip.yazilim.p2g.web.util.SpotifyHelper;

import java.io.IOException;

/**
 * @author mustafaarifsisman - 26.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class Profile implements IProfile {

    private final Logger LOGGER = LoggerFactory.getLogger(Profile.class);

    @Autowired
    @Qualifier(Constants.BEAN_NAME_CLIENT_CREDENTIALS)
    private SpotifyApi spotifyApi;

    @Override
    public vip.yazilim.p2g.web.entity.User getUser(String spotifyAccountId) {
        GetUsersProfileRequest getUsersProfileRequest = spotifyApi.getUsersProfile(spotifyAccountId).build();
        User spotifyUser;

        try {
            spotifyUser = getUsersProfileRequest.execute();
            return SpotifyHelper.spotifyUserToUser(spotifyUser);
        } catch (IOException | SpotifyWebApiException e) {
            LOGGER.error("Cannot get Spotify account with id[{}]", spotifyAccountId);
        }

        return null;
    }

}
