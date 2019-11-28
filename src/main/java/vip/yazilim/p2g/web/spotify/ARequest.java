package vip.yazilim.p2g.web.spotify;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.requests.data.AbstractDataRequest;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public abstract class ARequest {

    protected ARequest(){
    }

    public abstract AbstractDataRequest build(SpotifyApi spotifyApi);

}
