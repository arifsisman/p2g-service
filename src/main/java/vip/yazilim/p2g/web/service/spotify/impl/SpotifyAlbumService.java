package vip.yazilim.p2g.web.service.spotify.impl;

import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.TrackSimplified;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.exception.RequestException;
import vip.yazilim.p2g.web.model.SearchModel;
import vip.yazilim.p2g.web.service.spotify.ISpotifyAlbumService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyRequestService;

import javax.transaction.Transactional;
import java.util.LinkedList;
import java.util.List;

/**
 * @author mustafaarifsisman - 29.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Transactional
@Service
public class SpotifyAlbumService implements ISpotifyAlbumService {

    @Autowired
    private ISpotifyRequestService spotifyRequest;

    @Override
    public List<SearchModel> getSongs(String albumId) throws RequestException {
        List<SearchModel> searchModelList = new LinkedList<>();

        Paging<TrackSimplified> trackSimplifiedPaging = spotifyRequest.execRequestSync((spotifyApi) -> spotifyApi.getAlbumsTracks(albumId).build());
        TrackSimplified[] tracks = trackSimplifiedPaging.getItems();

        for(TrackSimplified t: tracks){
            SearchModel searchModel = new SearchModel(t);
            searchModelList.add(searchModel);
        }

        return searchModelList;
    }
}
