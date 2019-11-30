package vip.yazilim.p2g.web.service.spotify.impl;

import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.TrackSimplified;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.p2g.web.service.spotify.ISpotifyAlbumService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyRequestService;
import vip.yazilim.p2g.web.util.SpotifyHelper;

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
    public List<Song> getSongs(String albumId) {
        List<Song> songList = new LinkedList<>();

        Paging<TrackSimplified> trackSimplifiedPaging = spotifyRequest.execRequestSync((spotifyApi) -> spotifyApi.getAlbumsTracks(albumId).build());
        TrackSimplified[] tracks = trackSimplifiedPaging.getItems();

        for (TrackSimplified t : tracks) {
            songList.add(SpotifyHelper.trackToSong(t));
        }

        return songList;
    }
}
