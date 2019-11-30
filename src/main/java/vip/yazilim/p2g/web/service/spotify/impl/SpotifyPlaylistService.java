package vip.yazilim.p2g.web.service.spotify.impl;

import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.p2g.web.service.spotify.ISpotifyPlaylistService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyRequestService;
import vip.yazilim.p2g.web.util.SpotifyHelper;

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
    public List<Song> getSongs(String playlistId) {
        List<Song> songList = new LinkedList<>();

        Paging<PlaylistTrack> dataRequest = spotifyRequest.execRequestSync((spotifyApi -> spotifyApi.getPlaylistsTracks(playlistId).build()));
        PlaylistTrack[] tracks = dataRequest.getItems();

        for (PlaylistTrack t : tracks) {
            songList.add(SpotifyHelper.trackToSong(t.getTrack()));
        }

        return songList;
    }
}
