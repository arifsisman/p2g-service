package vip.yazilim.p2g.web.model;

import com.wrapper.spotify.enums.ModelObjectType;
import com.wrapper.spotify.model_objects.AbstractModelObject;
import com.wrapper.spotify.model_objects.specification.*;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author mustafaarifsisman - 29.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@AllArgsConstructor
@Data
public class SearchModel {
    private ModelObjectType type;
    private String name;
    private ArtistSimplified[] artists;
    private String albumName;
    private String id;
    private String uri;
    private Long durationMs;
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
        this.type = ModelObjectType.TRACK;
        this.name = track.getName();
        this.artists = track.getArtists();
        this.albumName = track.getAlbum().getName();
        this.id = track.getId();
        this.uri = track.getUri();
        this.durationMs = track.getDurationMs().longValue();
        Image[] images = track.getAlbum().getImages();

        if (images.length > 0) {
            this.imageUrl = images[0].getUrl();
        }
    }

    private void init(TrackSimplified trackSimplified) {
        this.type = ModelObjectType.TRACK;
        this.name = trackSimplified.getName();
        this.artists = trackSimplified.getArtists();
        this.id = trackSimplified.getId();
        this.uri = trackSimplified.getUri();
        this.durationMs = trackSimplified.getDurationMs().longValue();
        this.imageUrl = trackSimplified.getPreviewUrl();
    }

    private void init(PlaylistSimplified playlistSimplified) {
        this.type = ModelObjectType.PLAYLIST;
        this.name = playlistSimplified.getName();
        this.id = playlistSimplified.getId();
        this.uri = playlistSimplified.getUri();

        Image[] images = playlistSimplified.getImages();

        if (images.length > 0) {
            this.imageUrl = images[0].getUrl();
        }
    }

    private void init(AlbumSimplified albumSimplified) {
        this.type = ModelObjectType.ALBUM;
        this.name = albumSimplified.getName();
        this.id = albumSimplified.getId();
        this.uri = albumSimplified.getUri();

        Image[] images = albumSimplified.getImages();

        if (images.length > 0) {
            this.imageUrl = images[0].getUrl();
        }
    }

}
