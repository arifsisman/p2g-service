package vip.yazilim.p2g.web.service.spotify;

import com.wrapper.spotify.SpotifyApi;
import vip.yazilim.p2g.web.entity.SpotifyToken;

import java.util.List;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ISRequestService {

    SpotifyApi initAuthorizedApi(SpotifyToken token);

    <R> R execRequest(ARequestBuilder<R> request, boolean async);
    <R> R execRequest(ARequestBuilder<R> request, SpotifyToken token, boolean async);
    <R> void execRequestList(ARequestBuilder<R> request, List<SpotifyToken> spotifyTokenList, boolean async);

}
