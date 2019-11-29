package vip.yazilim.p2g.web.spotify;

import vip.yazilim.p2g.web.entity.Song;

import java.util.List;

/**
 * @author mustafaarifsisman - 29.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ITrack {

    Song getTrack(String id);
    List<Song> getSeveralTracks(String[] ids);

}
