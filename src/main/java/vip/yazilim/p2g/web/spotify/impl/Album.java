package vip.yazilim.p2g.web.spotify.impl;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.TrackSimplified;
import com.wrapper.spotify.requests.data.AbstractDataRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.p2g.web.spotify.ARequestBuilder;
import vip.yazilim.p2g.web.spotify.IAlbum;
import vip.yazilim.p2g.web.spotify.IRequest;
import vip.yazilim.p2g.web.util.SpotifyHelper;

import java.util.LinkedList;
import java.util.List;

/**
 * @author mustafaarifsisman - 29.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class Album implements IAlbum {

    @Autowired
    private IRequest spotifyRequest;

    @Override
    public List<Song> getSongs(String albumId) {
        List<Song> songList = new LinkedList<>();

        ARequestBuilder request = new ARequestBuilder() {
            @Override
            public AbstractDataRequest build(SpotifyApi spotifyApi) {
                return spotifyApi.getAlbumsTracks(albumId).build();
            }
        };

        AbstractDataRequest dataRequest = spotifyRequest.initRequest(request);
        Paging<?> trackSimplifiedPaging = (Paging<?>) spotifyRequest.execRequestSync(dataRequest);

        TrackSimplified[] tracks = (TrackSimplified[]) trackSimplifiedPaging.getItems();

        for (TrackSimplified t : tracks) {
            songList.add(SpotifyHelper.trackToSong(t));
        }

        return songList;
    }
}
