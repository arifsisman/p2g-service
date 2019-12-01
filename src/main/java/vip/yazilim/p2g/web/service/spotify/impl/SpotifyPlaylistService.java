package vip.yazilim.p2g.web.service.spotify.impl;

import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.exception.RequestException;
import vip.yazilim.p2g.web.model.SearchModel;
import vip.yazilim.p2g.web.service.spotify.ISpotifyPlaylistService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyRequestService;

import java.util.LinkedList;
import java.util.List;

/**
 * @author mustafaarifsisman - 29.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class SpotifyPlaylistService implements ISpotifyPlaylistService {

    @Autowired
    private ISpotifyRequestService spotifyRequest;

    @Override
    public List<SearchModel> getSongs(String playlistId) throws RequestException {
        List<SearchModel> searchModelList = new LinkedList<>();

        Paging<PlaylistTrack> dataRequest = spotifyRequest.execRequestSync((spotifyApi -> spotifyApi.getPlaylistsTracks(playlistId).build()));
        PlaylistTrack[] playlistTracks = dataRequest.getItems();

        for (PlaylistTrack p : playlistTracks) {
            SearchModel searchModel = new SearchModel(p.getTrack());
            searchModelList.add(searchModel);
        }

        return searchModelList;
    }
}
