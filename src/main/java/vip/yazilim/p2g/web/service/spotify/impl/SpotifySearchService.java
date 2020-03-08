package vip.yazilim.p2g.web.service.spotify.impl;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.special.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.yazilim.p2g.web.constant.enums.SearchType;
import vip.yazilim.p2g.web.model.SearchModel;
import vip.yazilim.p2g.web.service.p2g.ISpotifyTokenService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyRequestService;
import vip.yazilim.p2g.web.service.spotify.ISpotifySearchService;
import vip.yazilim.p2g.web.util.SecurityHelper;
import vip.yazilim.p2g.web.util.SpotifyHelper;
import vip.yazilim.spring.core.exception.database.DatabaseException;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static vip.yazilim.p2g.web.constant.enums.SearchType.SONG;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Transactional
@Service
public class SpotifySearchService implements ISpotifySearchService {

    @Autowired
    private ISpotifyRequestService spotifyRequest;

    @Autowired
    private ISpotifyTokenService tokenService;

    @Override
    public List<SearchModel> search(String q, SearchType... searchTypes) throws DatabaseException, IOException, SpotifyWebApiException {
        List<SearchModel> searchModelList = new LinkedList<>();

        String userId = SecurityHelper.getUserId();
        String accessToken = tokenService.getAccessTokenByUserId(userId);

        if (searchTypes.length == 0) {
            SearchResult songSearchResult = spotifyRequest.execRequestSync(spotifyApi -> spotifyApi.searchItem(q, SONG.getType()).limit(15).build(), accessToken);
            searchModelList.addAll(SpotifyHelper.convertAbstractModelObjectToSearchModelList(songSearchResult.getTracks().getItems()));
            return searchModelList;
        }

        for (SearchType s : searchTypes) {
            if (s == SearchType.SONG) {
                SearchResult songSearchResult = spotifyRequest.execRequestSync(spotifyApi -> spotifyApi.searchItem(q, SearchType.SONG.getType()).limit(15).build(), accessToken);
                searchModelList.addAll(SpotifyHelper.convertAbstractModelObjectToSearchModelList(songSearchResult.getTracks().getItems()));
            } else if (s == SearchType.ALBUM) {
                SearchResult songSearchResult = spotifyRequest.execRequestSync(spotifyApi -> spotifyApi.searchItem(q, SearchType.ALBUM.getType()).limit(5).build(), accessToken);
                searchModelList.addAll(SpotifyHelper.convertAbstractModelObjectToSearchModelList(songSearchResult.getAlbums().getItems()));
            } else if (s == SearchType.PLAYLIST) {
                SearchResult songSearchResult = spotifyRequest.execRequestSync(spotifyApi -> spotifyApi.searchItem(q, SearchType.PLAYLIST.getType()).limit(5).build(), accessToken);
                searchModelList.addAll(SpotifyHelper.convertAbstractModelObjectToSearchModelList(songSearchResult.getPlaylists().getItems()));
            }
        }

        return searchModelList;
    }
}
