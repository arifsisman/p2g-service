package vip.yazilim.p2g.web.spotify.impl;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.model_objects.special.SearchResult;
import com.wrapper.spotify.model_objects.specification.AlbumSimplified;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.requests.data.AbstractDataRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.constant.SearchTypes;
import vip.yazilim.p2g.web.entity.SpotifyToken;
import vip.yazilim.p2g.web.model.SearchModel;
import vip.yazilim.p2g.web.spotify.ARequestBuilder;
import vip.yazilim.p2g.web.spotify.IRequest;
import vip.yazilim.p2g.web.spotify.ISearch;
import vip.yazilim.p2g.web.util.SpotifyHelper;

import java.util.LinkedList;
import java.util.List;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class Search implements ISearch {

    @Autowired
    private IRequest spotifyRequest;

    @Override
    public List<SearchModel> search(SpotifyToken token, String q, SearchTypes... searchTypes) {
        List<SearchModel> searchModelList = new LinkedList<>();
        StringBuilder type = new StringBuilder();

        for (SearchTypes s : searchTypes) {
            type.append(s).append(",");
        }

        String finalType = type.toString();

        ARequestBuilder request = new ARequestBuilder() {
            @Override
            public AbstractDataRequest build(SpotifyApi spotifyApi) {
                return spotifyApi.searchItem(q, finalType).build();
            }
        };

        AbstractDataRequest dataRequest = spotifyRequest.initRequest(token, request);
        SearchResult searchResult = (SearchResult) spotifyRequest.execRequestSync(dataRequest);

        Track[] tracks;
        AlbumSimplified[] albums;
        PlaylistSimplified[] playlists;

        if (finalType.contains(SearchTypes.TRACK.getType())){
            tracks = searchResult.getTracks().getItems();

            if (tracks.length > 0) {
                for (Track t : tracks) {
                    SearchModel searchModel = new SearchModel();
                    searchModel.setType(SearchTypes.TRACK);
                    searchModel.setName(t.getName());
                    searchModel.setArtists(SpotifyHelper.artistsToArtistNameList(t.getArtists()));
                    searchModel.setImageUrl(t.getPreviewUrl());
                    searchModel.setUri(t.getId());
                    searchModel.setUri(t.getUri());

                    searchModelList.add(searchModel);
                }
            }
        }

        if (finalType.contains(SearchTypes.ALBUM.getType())){
            albums = searchResult.getAlbums().getItems();

            if (albums.length > 0) {
                for (AlbumSimplified a : albums) {
                    SearchModel searchModel = new SearchModel();
                    searchModel.setType(SearchTypes.ALBUM);
                    searchModel.setName(a.getName());
                    searchModel.setArtists(SpotifyHelper.artistsToArtistNameList(a.getArtists()));
                    searchModel.setImageUrl(a.getImages()[0].getUrl());
                    searchModel.setUri(a.getId());
                    searchModel.setUri(a.getUri());

                    searchModelList.add(searchModel);
                }
            }
        }

        if (finalType.contains(SearchTypes.PLAYLIST.getType())){
            playlists = searchResult.getPlaylists().getItems();

            if (playlists.length > 0) {
                for (PlaylistSimplified p : playlists) {
                    SearchModel searchModel = new SearchModel();
                    searchModel.setType(SearchTypes.PLAYLIST);
                    searchModel.setName(p.getName());
                    searchModel.setImageUrl(p.getImages()[0].getUrl());
                    searchModel.setUri(p.getId());
                    searchModel.setUri(p.getUri());

                    searchModelList.add(searchModel);
                }
            }
        }

        return searchModelList;
    }

}
