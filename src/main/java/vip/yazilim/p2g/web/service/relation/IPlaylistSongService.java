package vip.yazilim.p2g.web.service.relation;

import vip.yazilim.p2g.web.entity.relation.PlaylistSong;
import vip.yazilim.spring.utils.exception.DatabaseException;
import vip.yazilim.spring.utils.service.ICrudService;

import java.util.List;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IPlaylistSongService extends ICrudService<PlaylistSong, String> {

    List<PlaylistSong> getPlaylistSongListByPlaylist(String playlistUuid) throws DatabaseException;

}
