package vip.yazilim.p2g.web.service.spotify;

import vip.yazilim.p2g.web.exception.SpotifyException;
import vip.yazilim.p2g.web.model.SearchModel;

import java.util.List;

/**
 * @author mustafaarifsisman - 29.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ISpotifyAlbumService {

    List<SearchModel> getSongs(String albumId) throws SpotifyException;

}
