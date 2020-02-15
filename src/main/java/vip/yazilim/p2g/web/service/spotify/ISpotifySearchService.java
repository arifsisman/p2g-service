package vip.yazilim.p2g.web.service.spotify;


import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import vip.yazilim.p2g.web.constant.enums.SearchType;
import vip.yazilim.p2g.web.model.SearchModel;
import vip.yazilim.spring.core.exception.database.DatabaseException;

import java.io.IOException;
import java.util.List;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ISpotifySearchService {

    List<SearchModel> search(String q, SearchType... searchTypes) throws DatabaseException, IOException, SpotifyWebApiException;

}
