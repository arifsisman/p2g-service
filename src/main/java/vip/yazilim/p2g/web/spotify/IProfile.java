package vip.yazilim.p2g.web.spotify;


import vip.yazilim.p2g.web.entity.User;

/**
 * @author mustafaarifsisman - 26.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IProfile {

    User getUsersProfile(String spotifyAccountId);

}
