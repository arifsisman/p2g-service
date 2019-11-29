package vip.yazilim.p2g.web.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.constant.SearchTypes;
import vip.yazilim.p2g.web.entity.SpotifyToken;
import vip.yazilim.p2g.web.model.SearchModel;
import vip.yazilim.p2g.web.service.ISearchService;
import vip.yazilim.p2g.web.spotify.ISearch;

import java.util.List;

/**
 * @author mustafaarifsisman - 29.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class SearchService implements ISearchService {

    @Autowired
    private ISearch searchService;

    @Override
    public List<SearchModel> search(SpotifyToken token, String q, SearchTypes... searchTypes) {
        return searchService.search(token, q, searchTypes);
    }
}
