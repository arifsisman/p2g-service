package vip.yazilim.p2g.web.service.spotify;


import vip.yazilim.p2g.web.constant.enums.SearchType;
import vip.yazilim.p2g.web.model.SearchModel;

import java.util.List;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ISpotifySearchService {

    List<SearchModel> search(String q, SearchType... searchTypes);

}
