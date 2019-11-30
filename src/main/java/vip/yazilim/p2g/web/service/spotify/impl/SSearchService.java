package vip.yazilim.p2g.web.service.spotify.impl;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.enums.ModelObjectType;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.special.SearchResult;
import com.wrapper.spotify.model_objects.specification.AlbumSimplified;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import com.wrapper.spotify.model_objects.specification.Track;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.constant.Constants;
import vip.yazilim.p2g.web.constant.SearchTypes;
import vip.yazilim.p2g.web.model.SearchModel;
import vip.yazilim.p2g.web.service.spotify.ISRequestService;
import vip.yazilim.p2g.web.service.spotify.ISSearchService;
import vip.yazilim.p2g.web.util.SpotifyHelper;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class SSearchService implements ISSearchService {

    @Autowired
    private ISRequestService spotifyRequest;

    @Autowired
    @Qualifier(Constants.BEAN_NAME_CLIENT_CREDENTIALS)
    private SpotifyApi spotifyApi;

    @Override
    public List<SearchModel> search(String q, SearchTypes... searchTypes) throws IOException, SpotifyWebApiException {
        List<SearchModel> searchModelList = new LinkedList<>();

        //TODO: check api is working!!
        for (SearchTypes s : searchTypes) {
            if (s.type.equals(SearchTypes.TRACK.getType())) {
                searchModelList.addAll(searchSong(spotifyApi, q));
            }
            if (s.type.equals(SearchTypes.ALBUM.getType())) {
                searchModelList.addAll(searchAlbum(spotifyApi, q));
            }
            if (s.type.equals(SearchTypes.PLAYLIST.getType())) {
                searchModelList.addAll(searchPlaylist(spotifyApi, q));
            }
        }

        if (searchTypes.length == 0)
            searchModelList.addAll(searchSong(spotifyApi, q));

        return searchModelList;
    }

    private List<SearchModel> searchSong(SpotifyApi spotifyApi, String q) throws IOException, SpotifyWebApiException {
        SearchResult songSearchResult = spotifyApi.searchItem(q, ModelObjectType.TRACK.getType()).build().execute();
        Track[] tracks = songSearchResult.getTracks().getItems();
        List<SearchModel> searchModelList = new LinkedList<>();

        for (Track t : tracks) {
            SearchModel searchModel = new SearchModel();
            searchModel.setType(SearchTypes.TRACK);
            searchModel.setName(t.getName());
            searchModel.setArtists(SpotifyHelper.artistsToArtistNameList(t.getArtists()));
            searchModel.setImageUrl(t.getPreviewUrl());
            searchModel.setId(t.getId());
            searchModel.setUri(t.getUri());

            searchModelList.add(searchModel);
        }

        return searchModelList;
    }

    private List<SearchModel> searchAlbum(SpotifyApi spotifyApi, String q) throws IOException, SpotifyWebApiException {
        SearchResult searchResult = spotifyApi.searchItem(q, ModelObjectType.ALBUM.getType()).build().execute();
        AlbumSimplified[] albums = searchResult.getAlbums().getItems();
        List<SearchModel> searchModelList = new LinkedList<>();

        for (AlbumSimplified a : albums) {
            SearchModel searchModel = new SearchModel();
            searchModel.setType(SearchTypes.ALBUM);
            searchModel.setName(a.getName());
            searchModel.setArtists(SpotifyHelper.artistsToArtistNameList(a.getArtists()));
            searchModel.setImageUrl(a.getImages()[0].getUrl());
            searchModel.setId(a.getId());
            searchModel.setUri(a.getUri());

            searchModelList.add(searchModel);
        }

        return searchModelList;
    }

    private List<SearchModel> searchPlaylist(SpotifyApi spotifyApi, String q) throws IOException, SpotifyWebApiException {
        SearchResult searchResult = spotifyApi.searchItem(q, ModelObjectType.PLAYLIST.getType()).build().execute();
        PlaylistSimplified[] playlists = searchResult.getPlaylists().getItems();
        List<SearchModel> searchModelList = new LinkedList<>();

        for (PlaylistSimplified p : playlists) {
            SearchModel searchModel = new SearchModel();
            searchModel.setType(SearchTypes.PLAYLIST);
            searchModel.setName(p.getName());
            searchModel.setImageUrl(p.getImages()[0].getUrl());
            searchModel.setId(p.getId());
            searchModel.setUri(p.getUri());

            searchModelList.add(searchModel);
        }

        return searchModelList;
    }
}
