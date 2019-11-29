package vip.yazilim.p2g.web.spotify.impl;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import com.wrapper.spotify.requests.data.AbstractDataRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.p2g.web.spotify.ARequestBuilder;
import vip.yazilim.p2g.web.spotify.IPlaylist;
import vip.yazilim.p2g.web.spotify.IRequest;
import vip.yazilim.p2g.web.util.SpotifyHelper;

import java.util.LinkedList;
import java.util.List;

/**
 * @author mustafaarifsisman - 29.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class Playlist implements IPlaylist {

    @Autowired
    private IRequest spotifyRequest;

    @Override
    public List<Song> getSongs(String playlistId) {
        List<Song> songList = new LinkedList<>();

        ARequestBuilder request = new ARequestBuilder() {
            @Override
            public AbstractDataRequest build(SpotifyApi spotifyApi) {
                return spotifyApi.getPlaylistsTracks(playlistId).build();
            }
        };

        AbstractDataRequest dataRequest = spotifyRequest.initRequest(request);
        Paging<?> playlistTrackPaging = (Paging<?>) spotifyRequest.execRequestSync(dataRequest);

        PlaylistTrack[] tracks = (PlaylistTrack[]) playlistTrackPaging.getItems();

        for (PlaylistTrack t : tracks) {
            songList.add(SpotifyHelper.trackToSong(t.getTrack()));
        }

        return songList;
    }
}
