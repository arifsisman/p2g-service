package vip.yazilim.p2g.web.config;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
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
public class SpotifyConfig {

    @Value("${spotify.clientId}")
    private String clientId;

    @Value("${spotify.clientSecret}")
    private String clientSecret;

    @Value("${spotify.redirectUrl}")
    private String redirectUri;

    @Value("${spotify.refreshToken}")
    private String refreshToken;

    // For auth required Spotify requests
    @Bean(Constants.BEAN_NAME_REDIRECT_URI)
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public SpotifyApi spotifyApiRedirectUri() {

        URI redirectUri = SpotifyHttpManager.makeUri(this.redirectUri);

        return new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRedirectUri(redirectUri)
                .build();
    }

    @Bean(Constants.BEAN_NAME_AUTHORIZATION_CODE)
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public SpotifyApi spotifyApiAuthorizationCode() {

        URI redirectUri = SpotifyHttpManager.makeUri(this.redirectUri);

        return new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRedirectUri(redirectUri)
                .build();
    }

    // For refresh tokens
    @Bean(Constants.BEAN_NAME_SIMPLE)
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public SpotifyApi spotifyApiRefresh() {

        return new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRefreshToken(refreshToken)
                .build();
    }

    // For simple Spotify requests
    @Bean(Constants.BEAN_NAME_SIMPLE)
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public SpotifyApi spotifyApiSimple() {

        return new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .build();
    }

}
