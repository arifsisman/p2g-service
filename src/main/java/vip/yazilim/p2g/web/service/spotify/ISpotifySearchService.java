package vip.yazilim.p2g.web.service.spotify;


import com.wrapper.spotify.enums.ModelObjectType;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import vip.yazilim.p2g.web.entity.model.SearchModel;

import java.io.IOException;
import java.util.List;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ISpotifySearchService {

    List<SearchModel> search(String q, ModelObjectType... searchTypes) throws IOException, SpotifyWebApiException;

}
