package vip.yazilim.p2g.web.config.spotify;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import vip.yazilim.p2g.web.constant.Constants;
import vip.yazilim.p2g.web.entity.SpotifyToken;
import vip.yazilim.p2g.web.exception.TokenException;
import vip.yazilim.p2g.web.service.ITokenService;
import vip.yazilim.spring.utils.exception.DatabaseException;
import vip.yazilim.spring.utils.exception.InvalidUpdateException;

import java.io.IOException;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Controller
public class TokenRefresher {

    private final Logger LOGGER = LoggerFactory.getLogger(TokenRefresher.class);

    @Autowired
    @Qualifier(Constants.BEAN_NAME_AUTHORIZATION_CODE)
    private SpotifyApi spotifyApi;

    @Autowired
    private ITokenService tokenService;

    public void refreshToken(String userUuid) {
        Optional<SpotifyToken> tokenOptional;
        SpotifyToken token;

        try {
            tokenOptional = tokenService.getTokenByUserUuid(userUuid);
        } catch (DatabaseException e) {
            throw new TokenException("Error while fetching tokens!");
        }

        if (tokenOptional.isPresent()) {
            token = tokenOptional.get();
        } else {
            LOGGER.error("Token not found with userUuid[{}]", userUuid);
            return;
        }

        String refreshToken = token.getRefreshToken();

        try {
            if (refreshToken != null) {
                AuthorizationCodeCredentials authorizationCodeCredentials = spotifyApi.authorizationCodeRefresh()
                        .build().execute();

                String accessToken = authorizationCodeCredentials.getAccessToken();
                spotifyApi.setAccessToken(accessToken);

                tokenService.saveUserToken(userUuid, accessToken, refreshToken);

                LOGGER.info("Access token updated for userUuid[{}]", userUuid);
                LOGGER.info("Access Token: " + accessToken);
            }
        } catch (IOException | SpotifyWebApiException e) {
            throw new TokenException("Error while getting new access token!");
        } catch (InvalidUpdateException | DatabaseException e) {
            e.printStackTrace();
        }
    }

}
