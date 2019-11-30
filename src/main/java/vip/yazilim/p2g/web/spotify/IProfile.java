package vip.yazilim.p2g.web.spotify;


import com.wrapper.spotify.model_objects.specification.User;

/**
 * @author mustafaarifsisman - 26.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IProfile {

    User getSpotifyUser(String spotifyAccountId);

}
