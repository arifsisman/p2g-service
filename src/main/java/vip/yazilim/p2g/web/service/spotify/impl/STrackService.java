package vip.yazilim.p2g.web.service.spotify.impl;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.requests.data.AbstractDataRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.p2g.web.service.spotify.ARequestBuilder;
import vip.yazilim.p2g.web.service.spotify.ISRequestService;
import vip.yazilim.p2g.web.service.spotify.ISTrackService;
import vip.yazilim.p2g.web.util.SpotifyHelper;

import java.util.LinkedList;
import java.util.List;

/**
 * @author mustafaarifsisman - 29.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class STrackService implements ISTrackService {

    @Autowired
    private ISRequestService spotifyRequest;

    @Override
    public Song getTrack(String id) {
        ARequestBuilder<com.wrapper.spotify.model_objects.specification.Track> request
                = new ARequestBuilder<com.wrapper.spotify.model_objects.specification.Track>() {
            @Override
            public AbstractDataRequest<com.wrapper.spotify.model_objects.specification.Track> build(SpotifyApi spotifyApi) {
                return spotifyApi.getTrack(id).build();
            }
        };

        AbstractDataRequest<com.wrapper.spotify.model_objects.specification.Track> dataRequest
                = spotifyRequest.initRequest(request);

        com.wrapper.spotify.model_objects.specification.Track track;
        track = spotifyRequest.execRequestSync(dataRequest);

        return SpotifyHelper.trackToSong(track);
    }

    @Override
    public List<Song> getSeveralTracks(String[] ids) {
        List<Song> songList = new LinkedList<>();

        ARequestBuilder<com.wrapper.spotify.model_objects.specification.Track[]> request = new ARequestBuilder<com.wrapper.spotify.model_objects.specification.Track[]>() {
            @Override
            public AbstractDataRequest<com.wrapper.spotify.model_objects.specification.Track[]> build(SpotifyApi spotifyApi) {
                return spotifyApi.getSeveralTracks(ids).build();
            }
        };

        AbstractDataRequest<com.wrapper.spotify.model_objects.specification.Track[]> dataRequest = spotifyRequest.initRequest(request);
        com.wrapper.spotify.model_objects.specification.Track[] tracks;
        tracks = spotifyRequest.execRequestSync(dataRequest);

        for (com.wrapper.spotify.model_objects.specification.Track track : tracks) {
            songList.add(SpotifyHelper.trackToSong(track));
        }

        return songList;
    }
}
