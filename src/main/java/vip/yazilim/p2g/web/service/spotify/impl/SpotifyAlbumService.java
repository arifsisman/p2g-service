package vip.yazilim.p2g.web.service.spotify.impl;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.TrackSimplified;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.model.SearchModel;
import vip.yazilim.p2g.web.service.p2g.ISpotifyTokenService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyAlbumService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyRequestService;
import vip.yazilim.p2g.web.util.SecurityHelper;
import vip.yazilim.spring.core.exception.database.DatabaseException;

import javax.transaction.Transactional;
import java.io.IOException;
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

    @Autowired
    private ISpotifyTokenService tokenService;

    @Override
    public List<SearchModel> getSongs(String albumId) throws IOException, SpotifyWebApiException, DatabaseException {
        List<SearchModel> searchModelList = new LinkedList<>();

        String userId = SecurityHelper.getUserId();
        String accessToken = tokenService.getAccessTokenByUserId(userId);

        Paging<TrackSimplified> trackSimplifiedPaging = spotifyRequest.execRequestSync((spotifyApi) -> spotifyApi.getAlbumsTracks(albumId).build(), accessToken);
        TrackSimplified[] tracks = trackSimplifiedPaging.getItems();

        for(TrackSimplified t: tracks){
            SearchModel searchModel = new SearchModel(t);
            searchModelList.add(searchModel);
        }

        return searchModelList;
    }
}
