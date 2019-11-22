package vip.yazilim.p2g.web.config;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

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
    private String redirectUrl;

    @Value("${spotify.refreshToken}")
    private String refreshToken;

    @Bean("redirectUri")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public SpotifyApi spotifyApi() {

        URI redirectUri = SpotifyHttpManager.makeUri(redirectUrl);

        return new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRedirectUri(redirectUri)
                .build();
    }

    @Bean("refresh")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public SpotifyApi spotifyApiRefresh() {

        return new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRefreshToken(refreshToken)
                .build();
    }

    // For simple Spotify requests
    @Bean("simple")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public SpotifyApi spotifyApiSimple() {

        return new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .build();
    }

}
