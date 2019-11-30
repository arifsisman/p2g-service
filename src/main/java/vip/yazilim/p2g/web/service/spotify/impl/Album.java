package vip.yazilim.p2g.web.service.spotify.impl;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.TrackSimplified;
import com.wrapper.spotify.requests.data.AbstractDataRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.p2g.web.service.spotify.ARequestBuilder;
import vip.yazilim.p2g.web.service.spotify.IAlbum;
import vip.yazilim.p2g.web.service.spotify.IRequest;
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

        ARequestBuilder<Paging<TrackSimplified>> request = new ARequestBuilder<Paging<TrackSimplified>>() {
            @Override
            public AbstractDataRequest<Paging<TrackSimplified>> build(SpotifyApi spotifyApi) {
                return spotifyApi.getAlbumsTracks(albumId).build();
            }
        };

        AbstractDataRequest<Paging<TrackSimplified>> dataRequest = spotifyRequest.initRequest(request);
        Paging<TrackSimplified> trackSimplifiedPaging = spotifyRequest.execRequestSync(dataRequest);

        TrackSimplified[] tracks = trackSimplifiedPaging.getItems();

        for (TrackSimplified t : tracks) {
            songList.add(SpotifyHelper.trackToSong(t));
        }

        return songList;
    }
}
