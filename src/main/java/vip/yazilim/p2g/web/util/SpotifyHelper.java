package vip.yazilim.p2g.web.util;

import com.wrapper.spotify.model_objects.specification.Track;
import vip.yazilim.p2g.web.entity.Song;

import java.util.Arrays;

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
}
