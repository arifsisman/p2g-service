package vip.yazilim.p2g.web.service.spotify;


import vip.yazilim.p2g.web.model.SearchModel;

import java.util.List;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ISpotifySearchService {

    List<SearchModel> search(String query);

    List<SearchModel> getRecommendations();

    SearchModel getByTrackId(String id);

    List<SearchModel> getByAlbumId(String albumId);

    List<SearchModel> getByPlaylistId(String playlistId);
}
