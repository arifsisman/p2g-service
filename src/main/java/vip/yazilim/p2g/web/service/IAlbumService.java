package vip.yazilim.p2g.web.service;

import vip.yazilim.p2g.web.entity.Album;
import vip.yazilim.p2g.web.entity.Song;

import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IAlbumService extends ICrudService<Album, String> {

    Optional<List<Song>> getSongsByAlbumUuid(String albumUuid);
    Optional<String> getImageUrlByAlbumUuid(String albumUuid);

}
