package vip.yazilim.p2g.web.service.spotify.impl;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import com.wrapper.spotify.requests.data.AbstractDataRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.p2g.web.service.spotify.ARequestBuilder;
import vip.yazilim.p2g.web.service.spotify.ISPlaylistService;
import vip.yazilim.p2g.web.service.spotify.ISRequestService;
import vip.yazilim.p2g.web.util.SpotifyHelper;

import java.util.LinkedList;
import java.util.List;

/**
 * @author mustafaarifsisman - 29.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class SPlaylistService implements ISPlaylistService {

    @Autowired
    private ISRequestService spotifyRequest;

    @Override
    public List<Song> getSongs(String playlistId) {
        List<Song> songList = new LinkedList<>();

        ARequestBuilder<Paging<PlaylistTrack>> request = new ARequestBuilder<Paging<PlaylistTrack>>() {
            @Override
            public AbstractDataRequest<Paging<PlaylistTrack>> build(SpotifyApi spotifyApi) {
                return spotifyApi.getPlaylistsTracks(playlistId).build();
            }
        };

        AbstractDataRequest<Paging<PlaylistTrack>> dataRequest = spotifyRequest.initRequest(request);
        Paging<PlaylistTrack> playlistTrackPaging = spotifyRequest.execRequestSync(dataRequest);

        PlaylistTrack[] tracks = playlistTrackPaging.getItems();

        for (PlaylistTrack t : tracks) {
            songList.add(SpotifyHelper.trackToSong(t.getTrack()));
        }

        return songList;
    }
}
