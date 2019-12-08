package vip.yazilim.p2g.web.util;

import com.wrapper.spotify.enums.ModelObjectType;
import com.wrapper.spotify.model_objects.AbstractModelObject;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vip.yazilim.p2g.web.model.SearchModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mustafaarifsisman - 29.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public class SpotifyHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpotifyHelper.class);

    public static List<String> artistsToArtistNameList(ArtistSimplified[] artists) {
        List<String> artistList = new ArrayList<>();

        for (ArtistSimplified artist : artists) {
            artistList.add(artist.getName());
        }
        return artistList;
    }

    public static List<SearchModel> convertAbstractModelObjectToSearchModelList(AbstractModelObject[] abstractModelObjects) {
        List<SearchModel> searchModelList = new ArrayList<>();
        for (AbstractModelObject a : abstractModelObjects) {
            searchModelList.add(new SearchModel(a));
        }
        return searchModelList;
    }

    public static String convertIdToUri(String id, ModelObjectType type) {
        String uri = "spotify:";
        switch (type) {
            case TRACK:
                uri += ModelObjectType.TRACK.getType();
                break;
            case ALBUM:
                uri += ModelObjectType.ALBUM.getType();
                break;
            case PLAYLIST:
                uri += ModelObjectType.PLAYLIST.getType();
                break;
        }
        return uri + id;
    }

}
