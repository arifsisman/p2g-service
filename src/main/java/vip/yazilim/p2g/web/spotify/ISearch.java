package vip.yazilim.p2g.web.spotify;


import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import vip.yazilim.p2g.web.constant.SearchTypes;
import vip.yazilim.p2g.web.entity.SpotifyToken;
import vip.yazilim.p2g.web.model.SearchModel;

import java.io.IOException;
import java.util.List;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ISearch {

    List<SearchModel> search(SpotifyToken token, String q, SearchTypes... searchTypes) throws IOException, SpotifyWebApiException;

}
