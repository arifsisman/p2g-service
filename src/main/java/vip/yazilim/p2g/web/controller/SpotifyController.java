package vip.yazilim.p2g.web.controller;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import vip.yazilim.p2g.web.config.spotify.TokenRefreshScheduler;
import vip.yazilim.p2g.web.constant.Constants;
import vip.yazilim.p2g.web.entity.SpotifyToken;
import vip.yazilim.p2g.web.exception.TokenException;
import vip.yazilim.p2g.web.service.ITokenService;
import vip.yazilim.p2g.web.util.SecurityHelper;
import vip.yazilim.spring.utils.exception.DatabaseException;
import vip.yazilim.spring.utils.exception.InvalidUpdateException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;

/**
 * @author mustafaarifsisman - 23.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
//@EnableScheduling
//@EnableAsync
@Controller
public class SpotifyController {

    private final Logger LOGGER = LoggerFactory.getLogger(SpotifyController.class);

    @Autowired
    @Qualifier(Constants.BEAN_NAME_AUTHORIZATION_CODE)
    private SpotifyApi spotifyApi;

    @Autowired
    private AuthorizationCodeUriRequest authorizationCodeUriRequest;

    @Autowired
    private ITokenService tokenService;

//    private String userUuid;

//    TaskScheduler scheduler;

//    ScheduledFuture scheduledFuture;

    @Autowired
    private TokenRefreshScheduler scheduler;

    @GetMapping("/authorize")
    public void authorize(HttpServletResponse httpServletResponse) {
        URI uri = authorizationCodeUriRequest.execute();

        httpServletResponse.setHeader("Location", uri.toString());
        httpServletResponse.setStatus(302);
    }

    @GetMapping("/callback")
    @ResponseBody
    public SpotifyToken callback(@RequestParam String code) throws DatabaseException, InvalidUpdateException {

        AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(code).build();

        final AuthorizationCodeCredentials authorizationCodeCredentials;

        try {
            authorizationCodeCredentials = authorizationCodeRequest.execute();
        } catch (IOException | SpotifyWebApiException e) {
            throw new TokenException("Error while fetching tokens!");
        }

        String accessToken = authorizationCodeCredentials.getAccessToken();
        String refreshToken = authorizationCodeCredentials.getRefreshToken();
        Integer expireTime = authorizationCodeCredentials.getExpiresIn();

        spotifyApi.setAccessToken(accessToken);
        spotifyApi.setRefreshToken(refreshToken);

//        LOGGER.info("Access Token: " + accessToken);
//        LOGGER.info("Refresh Token: " + refreshToken);
//        LOGGER.info("Expire time: " + expireTime);

        String userUuid = SecurityHelper.getUserUuid();

        scheduler.getScheduler().scheduleAtFixedRate(() -> test(userUuid), 2000);
//        scheduler.getAsyncExecutor().execute(() -> test(userUuid));

        return tokenService.saveUserToken(userUuid, accessToken, refreshToken);
    }

    private void test(String user) {
//        try {
//            final String string = spotifyApi.startResumeUsersPlayback()
//                    .context_uri("spotify:album:5zT1JLIj9E57p3e1rFm9Uq")
//                    .build().execute();
//
//            System.out.println("Null: " + string);
//        } catch (IOException | SpotifyWebApiException e) {
//            System.out.println("Error: " + e.getMessage());
//        }

        LOGGER.info("User: " + user);
    }

    //TODO: Scheduler works for last login user, not all
//    @Scheduled(fixedRate = 3000000)
//    public void refreshToken(String userUuid) {
//
//        String refreshToken = spotifyApi.getRefreshToken();
//
//        try {
//            if (refreshToken != null) {
//                AuthorizationCodeCredentials authorizationCodeCredentials = spotifyApi.authorizationCodeRefresh()
//                        .build().execute();
//
//                String accessToken = authorizationCodeCredentials.getAccessToken();
//                spotifyApi.setAccessToken(accessToken);
////                LOGGER.info("Access Token: " + accessToken);
//
//                tokenService.saveUserToken(userUuid, accessToken, refreshToken);
////                LOGGER.info("Access token updated.");
//            }
//        } catch (IOException | SpotifyWebApiException e) {
//            throw new TokenException("Error while getting new access token!");
//        } catch (InvalidUpdateException | DatabaseException e) {
//            e.printStackTrace();
//        }
//    }


}
