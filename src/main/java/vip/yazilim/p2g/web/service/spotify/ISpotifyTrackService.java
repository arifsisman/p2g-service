package vip.yazilim.p2g.web.service.spotify;

import vip.yazilim.p2g.web.exception.RequestException;
import vip.yazilim.p2g.web.model.SearchModel;

import java.util.List;

/**
 * @author mustafaarifsisman - 29.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ISpotifyTrackService {

    SearchModel getTrack(String id) throws RequestException;

    List<SearchModel> getSeveralTracks(String[] ids) throws RequestException;

}