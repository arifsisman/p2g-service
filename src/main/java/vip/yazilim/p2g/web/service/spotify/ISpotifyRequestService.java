package vip.yazilim.p2g.web.service.spotify;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.requests.data.AbstractDataRequest;
import vip.yazilim.p2g.web.entity.SpotifyToken;
import vip.yazilim.p2g.web.entity.relation.UserDevice;
import vip.yazilim.p2g.web.exception.RequestException;

import java.util.List;
import java.util.function.Function;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ISpotifyRequestService {

    SpotifyApi initAuthorizedApi(SpotifyToken token);

    <R> R execRequestSync(Function<SpotifyApi, AbstractDataRequest<R>> dataRequestBuilder) throws RequestException;
    <R> R execRequestAsync(Function<SpotifyApi, AbstractDataRequest<R>> dataRequestBuilder) throws RequestException;

    <R> R execRequestSync(Function<SpotifyApi, AbstractDataRequest<R>> dataRequestBuilder, SpotifyToken token) throws RequestException;
    <R> R execRequestAsync(Function<SpotifyApi, AbstractDataRequest<R>> dataRequestBuilder, SpotifyToken token) throws RequestException;

    //-------------------------------
    <R> void execRequestListSync(RFunction<SpotifyApi, String, AbstractDataRequest<R>> dataRequestBuilder, List<SpotifyToken> spotifyTokenList, List<UserDevice> userDeviceList) throws RequestException;
    <R> void execRequestListAsync(RFunction<SpotifyApi, String, AbstractDataRequest<R>> dataRequestBuilder, List<SpotifyToken> spotifyTokenList, List<UserDevice> userDeviceList) throws RequestException;

}
