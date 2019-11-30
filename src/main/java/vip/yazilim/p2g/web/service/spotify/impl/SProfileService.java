package vip.yazilim.p2g.web.service.spotify.impl;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.model_objects.specification.User;
import com.wrapper.spotify.requests.data.AbstractDataRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.SpotifyToken;
import vip.yazilim.p2g.web.service.spotify.ARequestBuilder;
import vip.yazilim.p2g.web.service.spotify.ISProfileService;
import vip.yazilim.p2g.web.service.spotify.ISRequestService;

/**
 * @author mustafaarifsisman - 26.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class SProfileService implements ISProfileService {

    @Autowired
    private ISRequestService spotifyRequest;

    @Override
    public User getSpotifyUser(String spotifyAccountId) {
        User spotifyUser;

        ARequestBuilder<User> request = new ARequestBuilder<User>() {
            @Override
            public AbstractDataRequest<User> build(SpotifyApi spotifyApi) {
                return spotifyApi.getUsersProfile(spotifyAccountId).build();
            }
        };

        AbstractDataRequest<User> dataRequest = spotifyRequest.initRequest(request);
        spotifyUser = spotifyRequest.execRequestSync(dataRequest);

        return spotifyUser;
    }

    @Override
    public User getCurrentSpotifyUser(SpotifyToken spotifyToken) {
        User spotifyUser;

        ARequestBuilder<User> request = new ARequestBuilder<User>() {
            @Override
            public AbstractDataRequest<User> build(SpotifyApi spotifyApi) {
                return spotifyApi.getCurrentUsersProfile().build();

            }
        };

        AbstractDataRequest<User> dataRequest = spotifyRequest.initRequest(request, spotifyToken);
        spotifyUser = spotifyRequest.execRequestSync(dataRequest);

        return spotifyUser;
    }

}
