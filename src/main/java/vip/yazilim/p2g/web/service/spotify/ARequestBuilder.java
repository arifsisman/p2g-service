package vip.yazilim.p2g.web.service.spotify;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.requests.data.AbstractDataRequest;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public abstract class ARequestBuilder<R> {

    protected ARequestBuilder(){
    }

    public abstract AbstractDataRequest<R> build(SpotifyApi spotifyApi);

}
