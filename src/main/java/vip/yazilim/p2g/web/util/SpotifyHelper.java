package vip.yazilim.p2g.web.util;

import com.wrapper.spotify.model_objects.AbstractModelObject;
import com.wrapper.spotify.model_objects.miscellaneous.Device;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import vip.yazilim.p2g.web.constant.enums.Platform;
import vip.yazilim.p2g.web.constant.enums.SearchType;
import vip.yazilim.p2g.web.entity.UserDevice;
import vip.yazilim.p2g.web.model.SearchModel;

import java.util.LinkedList;
import java.util.List;

/**
 * @author mustafaarifsisman - 29.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public class SpotifyHelper {

    public static List<String> convertArtistsToArtistNameList(ArtistSimplified[] artists) {
        List<String> artistList = new LinkedList<>();
        for (ArtistSimplified artist : artists) {
            artistList.add(artist.getName());
        }
        return artistList;
    }

    public static List<SearchModel> convertAbstractModelObjectToSearchModelList(AbstractModelObject[] abstractModelObjects) {
        List<SearchModel> searchModelList = new LinkedList<>();
        for (AbstractModelObject a : abstractModelObjects) {
            searchModelList.add(new SearchModel(a));
        }
        return searchModelList;
    }

    public static String convertIdToUri(String id, SearchType type) {
        String uri = "spotify:";
        switch (type) {
            case SONG:
                uri += SearchType.SONG.getType();
                break;
            case ALBUM:
                uri += SearchType.ALBUM.getType();
                break;
            case PLAYLIST:
                uri += SearchType.PLAYLIST.getType();
                break;
        }
        return uri + id;
    }

    public static UserDevice convertDeviceToUserDevice(String userId, Device device) {
        UserDevice userDevice = new UserDevice();

        userDevice.setUserId(userId);
        userDevice.setId(device.getId());
        userDevice.setPlatform(Platform.SPOTIFY.getName());
        userDevice.setDeviceName(device.getName());
        userDevice.setDeviceType(device.getType());
        userDevice.setActiveFlag(device.getIs_active());

        return userDevice;
    }

}
