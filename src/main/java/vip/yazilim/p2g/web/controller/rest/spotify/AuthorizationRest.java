package vip.yazilim.p2g.web.controller.rest.spotify;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import vip.yazilim.p2g.web.config.spotify.TokenRefreshScheduler;
import vip.yazilim.p2g.web.config.spotify.TokenRefresher;
import vip.yazilim.p2g.web.constant.Constants;
import vip.yazilim.p2g.web.entity.OAuthToken;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.service.p2g.ISpotifyTokenService;
import vip.yazilim.p2g.web.service.p2g.IUserService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyUserService;
import vip.yazilim.p2g.web.util.SecurityHelper;
import vip.yazilim.spring.core.exception.general.InvalidArgumentException;
import vip.yazilim.spring.core.exception.general.InvalidUpdateException;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;
import vip.yazilim.spring.core.exception.web.NotFoundException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
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
    @Qualifier(Constants.BEAN_NAME_AUTHORIZATION_CODE)
    private SpotifyApi spotifyApi;

    @Autowired
    private AuthorizationCodeUriRequest authorizationCodeUriRequest;

    @Autowired
    private TokenRefreshScheduler tokenRefreshScheduler;

    @Autowired
    private TokenRefresher tokenRefresher;

    @Autowired
    private ISpotifyTokenService tokenService;

    @Autowired
    private IUserService userService;

    @Autowired
    private ISpotifyUserService spotifyUserService;

    @Deprecated
    @GetMapping("/authorize")
    public void authorize(HttpServletResponse httpServletResponse) {
        URI uri = authorizationCodeUriRequest.execute();

        httpServletResponse.setHeader("Location", uri.toString());
        httpServletResponse.setStatus(302);
    }

    @Deprecated
    @GetMapping("/callback")
    @ResponseBody
    public OAuthToken callback(@RequestParam String code) throws IOException, SpotifyWebApiException, DatabaseException, InvalidUpdateException, InvalidArgumentException {
        String userId = SecurityHelper.getUserId();
        Optional<User> userOpt = userService.getById(userId);

        if (!userOpt.isPresent()) {
            throw new NotFoundException("User not found");
        }

        AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(code).build();
        final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();

        String accessToken = authorizationCodeCredentials.getAccessToken();
        String refreshToken = authorizationCodeCredentials.getRefreshToken();

        spotifyApi.setAccessToken(accessToken);
        spotifyApi.setRefreshToken(refreshToken);

        // save users token
        OAuthToken token = tokenService.saveUserToken(userId, accessToken, refreshToken);
        // updates spotify infos every authorize
        userService.setSpotifyInfo(spotifyUserService.getCurrentSpotifyUser(userId), userOpt.get());

        // Set Token refresh scheduler
        int delayMs = 55 * 60 * 1000;
        Date now = new Date();
        Date startDate = new Date(now.getTime() + delayMs);

        tokenRefreshScheduler.getScheduler()
                .scheduleWithFixedDelay(() -> tokenRefresher.refreshToken(userId), startDate, delayMs);

        return token;
    }

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