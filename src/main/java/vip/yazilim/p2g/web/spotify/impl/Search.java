package vip.yazilim.p2g.web.spotify.impl;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.enums.ModelObjectType;
import com.wrapper.spotify.model_objects.special.SearchResult;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.requests.data.AbstractDataRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.p2g.web.entity.SpotifyToken;
import vip.yazilim.p2g.web.spotify.ARequest;
import vip.yazilim.p2g.web.spotify.IRequest;
import vip.yazilim.p2g.web.spotify.ISearch;
import vip.yazilim.p2g.web.util.SpotifyHelper;

import java.util.LinkedList;
import java.util.List;

/**
 * @author mustafaarifsism
 * an - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class Search implements ISearch {

    @Autowired
    private IRequest spotifyRequest;

    @Override
    public List<Song> searchSong(SpotifyToken token, String q) {
        List<Song> songList = new LinkedList<>();
        String type = ModelObjectType.TRACK.getType();

        ARequest request = new ARequest() {
            @Override
            public AbstractDataRequest build(SpotifyApi spotifyApi) {
                return spotifyApi.searchItem(q, type).build();
            }
        };

        AbstractDataRequest dataRequest = spotifyRequest.initRequest(token, request);
        SearchResult searchResult = (SearchResult) spotifyRequest.execRequest(dataRequest);
        
        Track[] tracks = searchResult.getTracks().getItems();

        for (Track t : tracks)
            songList.add(SpotifyHelper.trackToSong(t));

        return songList;
    }
}
