package vip.yazilim.p2g.web.spotify.impl;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.requests.data.AbstractDataRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.p2g.web.entity.SpotifyToken;
import vip.yazilim.p2g.web.spotify.ARequestBuilder;
import vip.yazilim.p2g.web.spotify.IRequest;
import vip.yazilim.p2g.web.spotify.ITrack;
import vip.yazilim.p2g.web.util.SpotifyHelper;

import java.util.LinkedList;
import java.util.List;

/**
 * @author mustafaarifsisman - 29.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class Track implements ITrack {

    @Autowired
    private IRequest spotifyRequest;

    @Override
    public Song getTrack(SpotifyToken token, String id) {
        ARequestBuilder request = new ARequestBuilder() {
            @Override
            public AbstractDataRequest build(SpotifyApi spotifyApi) {
                return spotifyApi.getTrack(id).build();
            }
        };

        AbstractDataRequest dataRequest = spotifyRequest.initRequest(token, request);
        com.wrapper.spotify.model_objects.specification.Track track;
        track = (com.wrapper.spotify.model_objects.specification.Track) spotifyRequest.execRequestSync(dataRequest);

        return SpotifyHelper.trackToSong(track);
    }

    @Override
    public List<Song> getSeveralTracks(SpotifyToken token, String[] ids) {
        List<Song> songList = new LinkedList<>();

        ARequestBuilder request = new ARequestBuilder() {
            @Override
            public AbstractDataRequest build(SpotifyApi spotifyApi) {
                return spotifyApi.getSeveralTracks(ids).build();
            }
        };

        AbstractDataRequest dataRequest = spotifyRequest.initRequest(token, request);
        com.wrapper.spotify.model_objects.specification.Track[] tracks;
        tracks = (com.wrapper.spotify.model_objects.specification.Track[]) spotifyRequest.execRequestSync(dataRequest);

        for (com.wrapper.spotify.model_objects.specification.Track track : tracks) {
            songList.add(SpotifyHelper.trackToSong(track));
        }

        return songList;
    }
}
