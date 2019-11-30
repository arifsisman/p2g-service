package vip.yazilim.p2g.web.config.spotify;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import vip.yazilim.p2g.web.entity.SpotifyToken;
import vip.yazilim.p2g.web.exception.TokenException;
import vip.yazilim.p2g.web.service.p2g.ITokenService;
import vip.yazilim.spring.utils.exception.DatabaseException;
import vip.yazilim.spring.utils.exception.InvalidUpdateException;

import java.io.IOException;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Configuration
public class TokenRefresher {

    private final Logger LOGGER = LoggerFactory.getLogger(TokenRefresher.class);

    @Value("${spotify.clientId}")
    private String clientId;

    @Value("${spotify.clientSecret}")
    private String clientSecret;

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
        if (refreshToken == null) {
            return;
        }

        try {
            SpotifyApi spotifyApi = new SpotifyApi.Builder()
                    .setClientId(clientId)
                    .setClientSecret(clientSecret)
                    .setRefreshToken(refreshToken)
                    .build();

            AuthorizationCodeCredentials authorizationCodeCredentials = spotifyApi.authorizationCodeRefresh()
                    .build().execute();

            String accessToken = authorizationCodeCredentials.getAccessToken();
            spotifyApi.setAccessToken(accessToken);

            tokenService.saveUserToken(userUuid, accessToken, refreshToken);

//                LOGGER.info("Access token updated for userUuid[{}]", userUuid);
//                LOGGER.info("Access Token: " + accessToken);
        } catch (IOException | SpotifyWebApiException e) {
            throw new TokenException("Error while getting new access token!");
        } catch (InvalidUpdateException | DatabaseException e) {
            e.printStackTrace();
        }
    }

}
