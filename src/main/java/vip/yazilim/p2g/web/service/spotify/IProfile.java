package vip.yazilim.p2g.web.service.spotify;


import com.wrapper.spotify.model_objects.specification.User;
import vip.yazilim.p2g.web.entity.SpotifyToken;

/**
 * @author mustafaarifsisman - 26.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IProfile {

    User getSpotifyUser(String spotifyAccountId);

    User getCurrentSpotifyUser(SpotifyToken spotifyToken);

}
