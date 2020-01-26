package vip.yazilim.p2g.web.service.spotify.impl;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.special.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.constant.Constants;
import vip.yazilim.p2g.web.constant.enums.SearchType;
import vip.yazilim.p2g.web.model.SearchModel;
import vip.yazilim.p2g.web.service.spotify.ISpotifySearchService;
import vip.yazilim.p2g.web.util.SpotifyHelper;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Transactional
@Service
public class SpotifySearchService implements ISpotifySearchService {

    @Autowired
    @Qualifier(Constants.BEAN_NAME_CLIENT_CREDENTIALS)
    private SpotifyApi spotifyApi;

    @Override
    public List<SearchModel> search(String q, SearchType... searchTypes) throws IOException, SpotifyWebApiException {
        List<SearchModel> searchModelList = new LinkedList<>();

        if (searchTypes.length == 0) {
            SearchResult songSearchResult = spotifyApi.searchItem(q, SearchType.TRACK.getType()).limit(20).build().execute();
            searchModelList.addAll(SpotifyHelper.convertAbstractModelObjectToSearchModelList(songSearchResult.getTracks().getItems()));
            return searchModelList;
        }

        for (SearchType s : searchTypes) {
            if (s == SearchType.TRACK) {
                SearchResult songSearchResult = spotifyApi.searchItem(q, s.getType()).limit(20).build().execute();
                searchModelList.addAll(SpotifyHelper.convertAbstractModelObjectToSearchModelList(songSearchResult.getTracks().getItems()));
            } else if (s == SearchType.ALBUM) {
                SearchResult songSearchResult = spotifyApi.searchItem(q, s.getType()).limit(10).build().execute();
                searchModelList.addAll(SpotifyHelper.convertAbstractModelObjectToSearchModelList(songSearchResult.getAlbums().getItems()));
            } else if (s == SearchType.PLAYLIST) {
                SearchResult songSearchResult = spotifyApi.searchItem(q, s.getType()).limit(10).build().execute();
                searchModelList.addAll(SpotifyHelper.convertAbstractModelObjectToSearchModelList(songSearchResult.getPlaylists().getItems()));
            }
        }

        return searchModelList;
    }
}
