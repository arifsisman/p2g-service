package vip.yazilim.p2g.web.model;

import com.wrapper.spotify.model_objects.AbstractModelObject;
import com.wrapper.spotify.model_objects.specification.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import vip.yazilim.p2g.web.constant.enums.SearchType;
import vip.yazilim.p2g.web.util.SpotifyHelper;

import java.io.Serializable;
import java.util.List;

/**
 * @author mustafaarifsisman - 29.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@AllArgsConstructor
@Data
public class SearchModel implements Serializable {
    private SearchType type;
    private String name;
    private List<String> artistNames;
    private String albumName;
    private String id;
    private String uri;
    private Integer durationMs;
    private String imageUrl;

    public SearchModel(AbstractModelObject object) {
        if (object instanceof TrackSimplified) {
            init((TrackSimplified) object);
        } else if (object instanceof Track) {
            init((Track) object);
        } else if (object instanceof PlaylistSimplified) {
            init((PlaylistSimplified) object);
        } else if (object instanceof AlbumSimplified) {
            init((AlbumSimplified) object);
        }
    }

    private void init(Track track) {
        this.type = SearchType.SONG;
        this.name = track.getName();
        this.artistNames = SpotifyHelper.convertArtistsToArtistNameList(track.getArtists());
        this.albumName = track.getAlbum().getName();
        this.id = track.getId();
        this.uri = track.getUri();
        this.durationMs = track.getDurationMs();

        Image[] images = track.getAlbum().getImages();
        if (images.length > 0) {
            this.imageUrl = images[0].getUrl();
        }
    }

    private void init(TrackSimplified trackSimplified) {
        this.type = SearchType.SONG;
        this.name = trackSimplified.getName();
        this.artistNames = SpotifyHelper.convertArtistsToArtistNameList(trackSimplified.getArtists());
        this.id = trackSimplified.getId();
        this.uri = trackSimplified.getUri();
        this.durationMs = trackSimplified.getDurationMs();
    }

    private void init(PlaylistSimplified playlistSimplified) {
        this.type = SearchType.PLAYLIST;
        this.name = playlistSimplified.getName();
        this.id = playlistSimplified.getId();
        this.uri = playlistSimplified.getUri();

        Image[] images = playlistSimplified.getImages();
        if (images.length > 0) {
            this.imageUrl = images[0].getUrl();
        }
    }

    private void init(AlbumSimplified albumSimplified) {
        this.type = SearchType.ALBUM;
        this.name = albumSimplified.getName();
        this.artistNames = SpotifyHelper.convertArtistsToArtistNameList(albumSimplified.getArtists());
        this.id = albumSimplified.getId();
        this.uri = albumSimplified.getUri();

        Image[] images = albumSimplified.getImages();
        if (images.length > 0) {
            this.imageUrl = images[0].getUrl();
        }
    }

}
