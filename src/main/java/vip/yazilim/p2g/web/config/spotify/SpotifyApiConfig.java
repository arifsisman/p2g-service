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
    private String redirectUri;

    // For auth required Spotify requests
    @Bean(Constants.BEAN_NAME_AUTHORIZATION_CODE)
    public SpotifyApi spotifyApiAuthenticationCode() {

        URI redirectUri = SpotifyHttpManager.makeUri(this.redirectUri);

        return new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRedirectUri(redirectUri)
                .build();
    }

    // For simple Spotify requests
    @Bean(Constants.BEAN_NAME_CLIENT_CREDENTIALS)
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public SpotifyApi spotifyApiClientCredentials() {

        return new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .build();
    }

    @Bean
    public AuthorizationCodeUriRequest init() {
        return spotifyApiAuthenticationCode().authorizationCodeUri()
//          .state("x4xkmn9pu3j6ukrs8n")
//          .scope("user-read-birthdate,user-read-email")
//          .show_dialog(true)
                .build();
    }
}
