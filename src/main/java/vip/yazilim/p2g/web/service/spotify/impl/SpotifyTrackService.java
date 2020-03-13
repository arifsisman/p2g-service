package vip.yazilim.p2g.web.service.spotify.impl;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.Track;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.model.SearchModel;
import vip.yazilim.p2g.web.service.p2g.ISpotifyTokenService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyRequestService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyTrackService;
import vip.yazilim.p2g.web.util.SecurityHelper;
import vip.yazilim.p2g.web.util.SpotifyHelper;
import vip.yazilim.spring.core.exception.database.DatabaseException;

import java.io.IOException;
import java.util.List;

/**
 * @author mustafaarifsisman - 29.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class SpotifyTrackService implements ISpotifyTrackService {

    @Autowired
    private ISpotifyRequestService spotifyRequest;

    @Autowired
    private ISpotifyTokenService tokenService;

    @Override
    public SearchModel getTrack(String id) throws IOException, SpotifyWebApiException, DatabaseException {
        String userId = SecurityHelper.getUserId();
        String accessToken = tokenService.getAccessTokenByUserId(userId);

        return new SearchModel(spotifyRequest.execRequestSync(spotifyApi -> spotifyApi.getTrack(id).build(), accessToken));
    }

    @Override
    public List<SearchModel> getSeveralTracks(String[] ids) throws IOException, SpotifyWebApiException, DatabaseException {
        String userId = SecurityHelper.getUserId();
        String accessToken = tokenService.getAccessTokenByUserId(userId);

        Track[] tracks = spotifyRequest.execRequestSync(spotifyApi -> spotifyApi.getSeveralTracks(ids).build(), accessToken);
        return SpotifyHelper.convertAbstractModelObjectToSearchModelList(tracks);
    }
}
