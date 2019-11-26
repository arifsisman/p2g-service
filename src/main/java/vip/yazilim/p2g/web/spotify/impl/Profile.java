package vip.yazilim.p2g.web.spotify.impl;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.model_objects.specification.User;
import com.wrapper.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;
import com.wrapper.spotify.requests.data.users_profile.GetUsersProfileRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.constant.Constants;
import vip.yazilim.p2g.web.spotify.IProfile;

import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * @author mustafaarifsisman - 26.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class Profile implements IProfile {

    private final Logger LOGGER = LoggerFactory.getLogger(Profile.class);

    @Autowired
    @Qualifier(Constants.BEAN_NAME_AUTHORIZATION_CODE)
    private SpotifyApi spotifyApi;

    public Optional<User> getCurrentUsersProfile() {
        GetCurrentUsersProfileRequest getCurrentUsersProfileRequest = spotifyApi.getCurrentUsersProfile().build();
        User user;

        try {
            final CompletableFuture<User> userFuture = getCurrentUsersProfileRequest.executeAsync();
            user = userFuture.join();
            return Optional.of(user);
        } catch (CompletionException e) {
            LOGGER.error("Error: " + e.getCause().getMessage());
        } catch (CancellationException e) {
            LOGGER.warn("Async operation cancelled.");
        }

        return Optional.empty();
    }

    @Override
    public Optional<User> getUsersProfile(String spotifyAccountId) {
        GetUsersProfileRequest getUsersProfileRequest = spotifyApi.getUsersProfile(spotifyAccountId).build();
        User user;

        try {
            final CompletableFuture<User> userFuture = getUsersProfileRequest.executeAsync();
            user = userFuture.join();
            return Optional.of(user);
        } catch (CompletionException e) {
            System.out.println("Error: " + e.getCause().getMessage());
        } catch (CancellationException e) {
            System.out.println("Async operation cancelled.");
        }

        return Optional.empty();
    }

}
