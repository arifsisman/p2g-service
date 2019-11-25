package vip.yazilim.p2g.web.spotify.flow;

/**
 * @author mustafaarifsisman - 30.10.2019
 * @contact mustafaarifsisman@gmail.com
 */

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import vip.yazilim.p2g.web.constant.Constants;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class AuthorizationCodeUri {

    @Autowired
    @Qualifier(Constants.BEAN_NAME_AUTHORIZATION_CODE)
    private SpotifyApi spotifyApi;

    private AuthorizationCodeUriRequest authorizationCodeUriRequest;

//    @Override
//    public void run(String... args) throws Exception {
////        RestTemplate template = new RestTemplate();
////
////        URI uri = authorizationCodeUriRequest.execute();
////        ResponseEntity<String> response = template.getForEntity(uri, String.class);
////        System.out.println("Response: " + response.getBody());
////        System.out.println("URI:" + uri.toString());
//    }
//
//    public void authorizationCodeUri_Async() {
//        try {
//            CompletableFuture<URI> uriFuture = authorizationCodeUriRequest.executeAsync();
//
//            // Thread free to do other tasks...
//
//            // Example Only. Never block in production code.
//            URI uri = uriFuture.join();
//
//            System.out.println("URI: " + uri.toString());
//        } catch (CompletionException e) {
//            System.out.println("Error: " + e.getCause().getMessage());
//        } catch (CancellationException e) {
//            System.out.println("Async operation cancelled.");
//        }
//    }
}