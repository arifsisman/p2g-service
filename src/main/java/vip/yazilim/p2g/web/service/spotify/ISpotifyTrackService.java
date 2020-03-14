package vip.yazilim.p2g.web.service.spotify;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import vip.yazilim.libs.springcore.exception.general.database.DatabaseException;
import vip.yazilim.p2g.web.model.SearchModel;

import java.io.IOException;
import java.util.List;

/**
 * @author mustafaarifsisman - 29.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ISpotifyTrackService {

    SearchModel getTrack(String id) throws IOException, SpotifyWebApiException, DatabaseException;

    List<SearchModel> getSeveralTracks(String[] ids) throws IOException, SpotifyWebApiException, DatabaseException;

}
