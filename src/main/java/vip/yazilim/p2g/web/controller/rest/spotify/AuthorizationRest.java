package vip.yazilim.p2g.web.controller.rest.spotify;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vip.yazilim.p2g.web.entity.OAuthToken;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.service.p2g.ISpotifyTokenService;
import vip.yazilim.p2g.web.service.p2g.IUserService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyUserService;
import vip.yazilim.p2g.web.util.SecurityHelper;
import vip.yazilim.spring.core.exception.general.InvalidArgumentException;
import vip.yazilim.spring.core.exception.general.InvalidUpdateException;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;

import java.io.IOException;
import java.util.Optional;

import static vip.yazilim.p2g.web.constant.Constants.API_SPOTIFY;

/**
 * @author mustafaarifsisman - 23.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@RestController
@RequestMapping(API_SPOTIFY)
public class AuthorizationRest {

    private Logger LOGGER = LoggerFactory.getLogger(AuthorizationRest.class);

    @Autowired
    private ISpotifyTokenService tokenService;

    @Autowired
    private IUserService userService;

    @Autowired
    private ISpotifyUserService spotifyUserService;

    @GetMapping("/login")
    public User login() throws DatabaseException, InvalidArgumentException, InvalidUpdateException, IOException, SpotifyWebApiException {
        String userId = SecurityHelper.getUserId();
        String userName = SecurityHelper.getUserDisplayName();
        Optional<User> userOpt = userService.getById(userId);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            LOGGER.info("{}[{}] logged in", userName, userId);
            updateUserAccessToken();
            return updateUserSpotifyInfos(user);
        } else {
            return register();
        }
    }

    @PostMapping("/token")
    public OAuthToken updateUserAccessToken(@RequestBody String accessToken) throws InvalidUpdateException, DatabaseException, InvalidArgumentException {
        String userId = SecurityHelper.getUserId();
        return tokenService.saveUserToken(userId, accessToken);
    }

    private User register() throws InvalidUpdateException, DatabaseException, InvalidArgumentException, IOException, SpotifyWebApiException {
        String userId = SecurityHelper.getUserId();
        String email = SecurityHelper.getUserEmail();
        String userName = SecurityHelper.getUserDisplayName();

        User user = userService.createUser(userId, email, userName, "0");

        LOGGER.info("{}[{}] registered", userName, userId);
        updateUserAccessToken();
        return updateUserSpotifyInfos(user);
    }

    private User updateUserSpotifyInfos(User user) throws DatabaseException, IOException, SpotifyWebApiException, InvalidArgumentException {
        return userService.setSpotifyInfo(spotifyUserService.getCurrentSpotifyUser(user.getId()), user);
    }

    private OAuthToken updateUserAccessToken() throws DatabaseException, InvalidArgumentException, InvalidUpdateException {
        return tokenService.saveUserToken(SecurityHelper.getUserId(), SecurityHelper.getUserAccessToken());
    }

}