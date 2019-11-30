package vip.yazilim.p2g.web.config.spotify;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import com.wrapper.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import vip.yazilim.p2g.web.constant.Constants;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;

/**
 * @author mustafaarifsisman - 30.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Configuration
public class SpotifyApiConfig {

    private final Logger LOGGER = LoggerFactory.getLogger(SpotifyApiConfig.class);

    @Value("${spotify.clientId}")
    private String clientId;

    @Value("${spotify.clientSecret}")
    private String clientSecret;

    @Value("${spotify.redirectUrl}")
    private String redirectUrl;

    // For Spotify requests that require authorization
    @Bean(Constants.BEAN_NAME_AUTHORIZATION_CODE)
    public SpotifyApi spotifyApiAuthorizationCode() {
        URI redirectUri = SpotifyHttpManager.makeUri(redirectUrl);

        return new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRedirectUri(redirectUri)
                .build();
    }

    // For build AuthorizationCodeUriRequest and define scope
    @Bean
    public AuthorizationCodeUriRequest getUri() {
        return spotifyApiAuthorizationCode()
                .authorizationCodeUri()
                .scope(Constants.SCOPE)
                .build();
    }

    // For Spotify requests that does not require authorization
    @Bean(Constants.BEAN_NAME_CLIENT_CREDENTIALS)
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    @PostConstruct
    public SpotifyApi spotifyApiClientCredentials() {

        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .build();

        final ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials().build();

        try {
            final ClientCredentials clientCredentials = clientCredentialsRequest.execute();

            // Set access token for further "spotifyApi" object usage
            spotifyApi.setAccessToken(clientCredentials.getAccessToken());
        } catch (IOException | SpotifyWebApiException e) {
            LOGGER.error("Error: " + e.getMessage());
        }

        return spotifyApi;
    }

}
