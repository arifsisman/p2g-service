package vip.yazilim.p2g.web.util;

import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.model_objects.specification.TrackSimplified;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vip.yazilim.p2g.web.entity.Song;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author mustafaarifsisman - 29.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public class SpotifyHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpotifyHelper.class);

    public static Song trackToSong(TrackSimplified track) {
        return getSong(track.getUri(), track.getId(), track.getName(), track.getArtists(), track.getDurationMs());
    }

    public static Song trackToSong(Track track) {
        return getSong(track.getUri(), track.getId(), track.getName(), track.getArtists(), track.getDurationMs());
    }

    private static Song getSong(String uri, String id, String name, ArtistSimplified[] artists, Integer durationMs) {
        Song song = new Song();

        song.setUri(uri);
        song.setSongId(id);
        song.setName(name);
        song.setArtists(Arrays.toString(artists));
        song.setDurationMs(durationMs);

        return song;
    }

    public static List<String> artistsToArtistNameList(ArtistSimplified[] artists){
        List<String> artistList = new ArrayList<>();

        for (ArtistSimplified artist: artists) {
            artistList.add(artist.getName());
        }

        return artistList;
    }

}
