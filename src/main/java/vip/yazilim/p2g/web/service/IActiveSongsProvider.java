package vip.yazilim.p2g.web.service;

import vip.yazilim.p2g.web.entity.Song;

import java.util.List;

/**
 * @author mustafaarifsisman - 09.04.2020
 * @contact mustafaarifsisman@gmail.com
 */
public interface IActiveSongsProvider {
    List<Song> getActiveSongs();

    void activateSong(Song song);

    void deactivateSong(Song song);
}
