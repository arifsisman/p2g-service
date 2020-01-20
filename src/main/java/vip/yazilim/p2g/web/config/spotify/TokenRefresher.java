package vip.yazilim.p2g.web.config.spotify;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import vip.yazilim.p2g.web.entity.OAuthToken;
import vip.yazilim.p2g.web.service.p2g.ISpotifyTokenService;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;

import java.util.Optional;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Deprecated
@Configuration
public class TokenRefresher {

    private final Logger LOGGER = LoggerFactory.getLogger(TokenRefresher.class);

    @Value("${spotify.clientId}")
    private String clientId;

    @Value("${spotify.clientSecret}")
    private String clientSecret;

    @Autowired
    private ISpotifyTokenService tokenService;

    public void refreshToken(String userId){
        Optional<OAuthToken> tokenOptional;
        OAuthToken token;

        try {
            tokenOptional = tokenService.getTokenByUserId(userId);
        } catch (DatabaseException e) {
            LOGGER.error("Error while fetching tokens!");
            return;
        }

        if (tokenOptional.isPresent()) {
            token = tokenOptional.get();
        } else {
            LOGGER.error("Token not found with userId[{}]", userId);
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

            tokenService.saveUserToken(userId, accessToken, refreshToken);

//                LOGGER.debug("Access token updated for userId[{}]", userId);
//                LOGGER.debug("Access Token: " + accessToken);
        } catch (Exception e) {
            LOGGER.error("Error while getting new access token!");
        }
    }

}
