package vip.yazilim.p2g.web.service.spotify.impl;

import com.wrapper.spotify.model_objects.specification.Track;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.p2g.web.service.spotify.ISpotifyRequestService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyTrackService;
import vip.yazilim.p2g.web.util.SpotifyHelper;

import java.util.LinkedList;
import java.util.List;

/**
 * @author mustafaarifsisman - 29.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class SpotifyTrackService implements ISpotifyTrackService {

    @Autowired
    private ISpotifyRequestService spotifyRequest;

    @Override
    public Song getTrack(String id) {
        Track track = spotifyRequest.execRequestSync((spotifyApi) -> spotifyApi.getTrack(id).build());
        return SpotifyHelper.trackToSong(track);
    }

    @Override
    public List<Song> getSeveralTracks(String[] ids) {
        List<Song> songList = new LinkedList<>();

        Track[] tracks = spotifyRequest.execRequestSync((spotifyApi) -> spotifyApi.getSeveralTracks(ids).build());

        for (com.wrapper.spotify.model_objects.specification.Track track : tracks) {
            songList.add(SpotifyHelper.trackToSong(track));
        }

        return songList;
    }
}
