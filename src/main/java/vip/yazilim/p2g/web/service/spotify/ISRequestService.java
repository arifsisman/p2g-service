package vip.yazilim.p2g.web.service.spotify;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.requests.data.AbstractDataRequest;
import vip.yazilim.p2g.web.entity.SpotifyToken;

import java.util.List;
import java.util.function.Function;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ISRequestService {

    SpotifyApi initAuthorizedApi(SpotifyToken token);

    <R> R execRequestSync(Function<SpotifyApi, AbstractDataRequest<R>> dataRequestBuilder);
    <R> R execRequestAsync(Function<SpotifyApi, AbstractDataRequest<R>> dataRequestBuilder);

    <R> R execRequestSync(Function<SpotifyApi, AbstractDataRequest<R>> dataRequestBuilder, SpotifyToken token);
    <R> R execRequestAsync(Function<SpotifyApi, AbstractDataRequest<R>> dataRequestBuilder, SpotifyToken token);

    //-------------------------------
    <R> void execRequestListSync(Function<SpotifyApi, AbstractDataRequest<R>> dataRequestBuilder, List<SpotifyToken> spotifyTokenList);
    <R> void execRequestListAsync(Function<SpotifyApi, AbstractDataRequest<R>> dataRequestBuilder, List<SpotifyToken> spotifyTokenList);

}
