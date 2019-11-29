package vip.yazilim.p2g.web.service;

import vip.yazilim.p2g.web.constant.SearchTypes;
import vip.yazilim.p2g.web.entity.SpotifyToken;
import vip.yazilim.p2g.web.model.SearchModel;

import java.util.List;

/**
 * @author mustafaarifsisman - 29.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ISearchService {

    List<SearchModel> search(SpotifyToken token, String q, SearchTypes... searchTypes);

}
