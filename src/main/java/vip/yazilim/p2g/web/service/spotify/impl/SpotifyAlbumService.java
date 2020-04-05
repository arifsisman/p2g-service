package vip.yazilim.p2g.web.service.spotify.impl;

import com.wrapper.spotify.model_objects.specification.Album;
import com.wrapper.spotify.model_objects.specification.Image;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.TrackSimplified;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.model.SearchModel;
import vip.yazilim.p2g.web.service.spotify.ISpotifyAlbumService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyRequestService;
import vip.yazilim.p2g.web.util.SecurityHelper;

import java.util.LinkedList;
import java.util.List;

/**
 * @author mustafaarifsisman - 29.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class SpotifyAlbumService implements ISpotifyAlbumService {

    @Autowired
    private ISpotifyRequestService spotifyRequest;

    @Override
    public List<SearchModel> getSongs(String albumId) {
        List<SearchModel> searchModelList = new LinkedList<>();

        String accessToken = SecurityHelper.getUserAccessToken();

        Album album = spotifyRequest.execRequestSync((spotifyApi) -> spotifyApi.getAlbum(albumId).build(), accessToken);

        Paging<TrackSimplified> trackSimplifiedPaging = spotifyRequest.execRequestSync((spotifyApi) -> spotifyApi.getAlbumsTracks(albumId).build(), accessToken);
        TrackSimplified[] tracks = trackSimplifiedPaging.getItems();

        String imageUrl = null;
        Image[] albumImages = album.getImages();
        if(albumImages.length > 0 ){
            imageUrl = albumImages[0].getUrl();
        }

        for(TrackSimplified t: tracks){
            SearchModel searchModel = new SearchModel(t);
            searchModel.setImageUrl(imageUrl);
            searchModelList.add(searchModel);
        }

        return searchModelList;
    }
}
