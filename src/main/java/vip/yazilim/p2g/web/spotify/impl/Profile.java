package vip.yazilim.p2g.web.spotify.impl;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.model_objects.specification.User;
import com.wrapper.spotify.requests.data.AbstractDataRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.spotify.ARequestBuilder;
import vip.yazilim.p2g.web.spotify.IProfile;
import vip.yazilim.p2g.web.spotify.IRequest;
import vip.yazilim.p2g.web.util.SpotifyHelper;

/**
 * @author mustafaarifsisman - 26.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class Profile implements IProfile {

    @Autowired
    private IRequest spotifyRequest;

    @Override
    public vip.yazilim.p2g.web.entity.User getUser(String spotifyAccountId) {
        User spotifyUser;

        ARequestBuilder request = new ARequestBuilder() {
            @Override
            public AbstractDataRequest build(SpotifyApi spotifyApi) {
                return spotifyApi.getUsersProfile(spotifyAccountId).build();
            }
        };

        AbstractDataRequest dataRequest = spotifyRequest.initRequest(request);
        spotifyUser = (User) spotifyRequest.execRequestSync(dataRequest);

        return SpotifyHelper.spotifyUserToUser(spotifyUser);
    }

}
