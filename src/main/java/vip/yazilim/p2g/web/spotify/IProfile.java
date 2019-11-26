package vip.yazilim.p2g.web.spotify;

import com.wrapper.spotify.model_objects.specification.User;

import java.util.Optional;

/**
 * @author mustafaarifsisman - 26.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IProfile {

    Optional<User> getCurrentUsersProfile();
    Optional<User> getUsersProfile(String spotifyAccountId);

}
