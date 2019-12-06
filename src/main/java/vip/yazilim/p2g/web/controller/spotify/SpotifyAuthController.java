package vip.yazilim.p2g.web.controller.spotify;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import vip.yazilim.p2g.web.config.spotify.TokenRefreshScheduler;
import vip.yazilim.p2g.web.config.spotify.TokenRefresher;
import vip.yazilim.p2g.web.constant.Constants;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.entity.relation.SpotifyToken;
import vip.yazilim.p2g.web.service.p2g.ITokenService;
import vip.yazilim.p2g.web.service.p2g.IUserService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyUserService;
import vip.yazilim.p2g.web.util.SecurityHelper;
import vip.yazilim.spring.core.exception.web.NotFoundException;
import vip.yazilim.spring.core.exception.web.ServiceException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 23.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@RestController
public class SpotifyAuthController {

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
    private ITokenService tokenService;

    @Autowired
    private IUserService userService;

    @Autowired
    private ISpotifyUserService spotifyUserService;

    @GetMapping("/authorize")
    public void authorize(HttpServletResponse httpServletResponse) {
        URI uri = authorizationCodeUriRequest.execute();

        httpServletResponse.setHeader("Location", uri.toString());
        httpServletResponse.setStatus(302);
    }

    @GetMapping("/callback")
    @ResponseBody
    public SpotifyToken callback(@RequestParam String code) {
        String userUuid = SecurityHelper.getUserUuid();
        Optional<User> userOpt = userService.getUserByUuid(userUuid);

        if (!userOpt.isPresent()) {
            throw new NotFoundException("User not found");
        }

        AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(code).build();
        final AuthorizationCodeCredentials authorizationCodeCredentials;

        try {
            authorizationCodeCredentials = authorizationCodeRequest.execute();
        } catch (IOException | SpotifyWebApiException e) {
            throw new ServiceException("Error while fetching tokens!", e);
        }

        String accessToken = authorizationCodeCredentials.getAccessToken();
        String refreshToken = authorizationCodeCredentials.getRefreshToken();

        spotifyApi.setAccessToken(accessToken);
        spotifyApi.setRefreshToken(refreshToken);

        SpotifyToken token;

        try {
            // save users token
            token = tokenService.saveUserToken(userUuid, accessToken, refreshToken);
            // updates spotify infos every authorize
            userService.setSpotifyInfo(spotifyUserService.getCurrentSpotifyUser(userUuid), userOpt.get());
        } catch (Exception e) {
            throw new ServiceException(e);
        }

        // Set Token refresh scheduler
        int delayMs = 55 * 60 * 1000;
        Date now = new Date();
        Date startDate = new Date(now.getTime() + delayMs);

        tokenRefreshScheduler.getScheduler()
                .scheduleWithFixedDelay(() -> tokenRefresher.refreshToken(userUuid), startDate, delayMs);

        return token;
    }
}
