package vip.yazilim.p2g.web.service.p2g.impl;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.constant.SearchTypes;
import vip.yazilim.p2g.web.model.SearchModel;
import vip.yazilim.p2g.web.service.p2g.ISearchService;
import vip.yazilim.p2g.web.service.spotify.ISearch;

import java.io.IOException;
import java.util.List;

/**
 * @author mustafaarifsisman - 29.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class SearchService implements ISearchService {

    private Logger LOGGER = LoggerFactory.getLogger(SearchService.class);

    @Autowired
    private ISearch searchService;

    @Override
    public List<SearchModel> search(String q, SearchTypes... searchTypes) {
        try {
            return searchService.search(q, searchTypes);
        } catch (IOException | SpotifyWebApiException e) {
            LOGGER.info("An error occurred while searching with query [{}]", q);
            return null;
        }
    }
}
