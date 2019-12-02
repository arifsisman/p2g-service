package vip.yazilim.p2g.web.service.spotify;

import vip.yazilim.p2g.web.entity.model.SearchModel;
import vip.yazilim.p2g.web.exception.RequestException;

import java.util.List;

/**
 * @author mustafaarifsisman - 29.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ISpotifyPlaylistService {

    List<SearchModel> getSongs(String playlistId) throws RequestException;

}
