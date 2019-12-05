package vip.yazilim.p2g.web.service.spotify;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.requests.data.AbstractDataRequest;
import vip.yazilim.p2g.web.exception.RequestException;
import vip.yazilim.p2g.web.model.service.PlayerModel;

import java.util.function.Function;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ISpotifyRequestService {

    SpotifyApi initAuthorizedApi(String accessToken);

    <R> R execRequestSync(Function<SpotifyApi, AbstractDataRequest<R>> dataRequestBuilder) throws RequestException;
    <R> R execRequestAsync(Function<SpotifyApi, AbstractDataRequest<R>> dataRequestBuilder) throws RequestException;

    <R> R execRequestSync(Function<SpotifyApi, AbstractDataRequest<R>> dataRequestBuilder, String accessToken) throws RequestException;
    <R> void execRequestAsync(Function<SpotifyApi, AbstractDataRequest<R>> dataRequestBuilder, String accessToken) throws RequestException;

    //-------------------------------
    <R> void execRequestListSync(RFunction<SpotifyApi, String, AbstractDataRequest<R>> dataRequestBuilder, PlayerModel playerModel) throws RequestException;
    <R> void execRequestListAsync(RFunction<SpotifyApi, String, AbstractDataRequest<R>> dataRequestBuilder, PlayerModel playerModel) throws RequestException;

}
