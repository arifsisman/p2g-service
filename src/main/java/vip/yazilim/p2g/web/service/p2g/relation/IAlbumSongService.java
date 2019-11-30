package vip.yazilim.p2g.web.service.p2g.relation;

import vip.yazilim.p2g.web.entity.relation.AlbumSong;
import vip.yazilim.spring.utils.exception.DatabaseException;
import vip.yazilim.spring.utils.service.ICrudService;

import java.util.List;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IAlbumSongService extends ICrudService<AlbumSong, String> {

    List<AlbumSong> getAlbumSongListByAlbum(String albumUuid) throws DatabaseException;

}
