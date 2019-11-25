package vip.yazilim.p2g.web.config.spotify;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import vip.yazilim.p2g.web.constant.Constants;

import java.net.URI;

/**
 * @author mustafaarifsisman - 30.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Configuration
public class SpotifyApiConfig {

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
    public SpotifyApi spotifyApiClientCredentials() {

        return new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .build();
    }

}
