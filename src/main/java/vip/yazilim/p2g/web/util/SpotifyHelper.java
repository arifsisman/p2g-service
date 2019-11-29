package vip.yazilim.p2g.web.util;

import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.Track;
import vip.yazilim.p2g.web.entity.Song;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author mustafaarifsisman - 29.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public class SpotifyHelper {

    public static Song trackToSong(Track track) {
        Song song = new Song();

        song.setUri(track.getUri());
        song.setSongId(track.getId());
        song.setName(track.getName());
        song.setArtists(Arrays.toString(track.getArtists()));
        song.setDurationMs(track.getDurationMs());

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
