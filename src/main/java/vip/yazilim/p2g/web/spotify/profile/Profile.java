package vip.yazilim.p2g.web.spotify.profile;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.User;
import com.wrapper.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Controller;
import vip.yazilim.p2g.web.constant.Constants;
import vip.yazilim.p2g.web.controller.SpotifyController;

import java.io.IOException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * @author mustafaarifsisman - 26.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Controller
public class Profile {

    private final Logger LOGGER = LoggerFactory.getLogger(Profile.class);

    @Autowired
    @Qualifier(Constants.BEAN_NAME_AUTHORIZATION_CODE)
    private SpotifyApi spotifyApi;

    public void getCurrentUsersProfile() {
        GetCurrentUsersProfileRequest getCurrentUsersProfileRequest = spotifyApi.getCurrentUsersProfile().build();

//        sync
//        try {
//            final User user = getCurrentUsersProfileRequest.execute();
//
//            System.out.println("Display name: " + user.getDisplayName());
//        } catch (IOException | SpotifyWebApiException e) {
//            System.out.println("Error: " + e.getMessage());
//        }

        //async
        try {
            final CompletableFuture<User> userFuture = getCurrentUsersProfileRequest.executeAsync();

            // Thread free to do other tasks...

            // Example Only. Never block in production code.
            final User user = userFuture.join();

            LOGGER.info("Display name: " + user.getDisplayName());
        } catch (CompletionException e) {
            LOGGER.error("Error: " + e.getCause().getMessage());
        } catch (CancellationException e) {
            LOGGER.warn("Async operation cancelled.");
        }
    }

}
