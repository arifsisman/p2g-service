package vip.yazilim.p2g.web.spotify;


import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.p2g.web.entity.SpotifyToken;

import java.util.List;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ISearch {

    List<Song> searchSong(SpotifyToken token, String q);

}
