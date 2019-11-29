package vip.yazilim.p2g.web.spotify;

import com.wrapper.spotify.requests.data.AbstractDataRequest;
import vip.yazilim.p2g.web.entity.SpotifyToken;

import java.util.List;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IRequest {

    List<AbstractDataRequest> initRequestList(List<SpotifyToken> spotifyTokenList, ARequest request);
    void execRequestListAsync(List<AbstractDataRequest> requestList);
    void execRequestListSync(List<AbstractDataRequest> requestList);

    Object execSingleRequest(SpotifyToken token, ARequest request);
}
