package vip.yazilim.p2g.web.service.spotify;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.requests.data.AbstractDataRequest;
import vip.yazilim.p2g.web.service.spotify.model.RequestFunction;

import java.io.IOException;
import java.util.HashMap;
import java.util.function.Function;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ISpotifyRequestService {

    SpotifyApi initAuthorizedApi(String accessToken);

//    <R> R execRequestSync(Function<SpotifyApi, AbstractDataRequest<R>> dataRequestBuilder) throws IOException, SpotifyWebApiException;
//    <R> void execRequestAsync(Function<SpotifyApi, AbstractDataRequest<R>> dataRequestBuilder) throws IOException, SpotifyWebApiException;

    <R> R execRequestSync(Function<SpotifyApi, AbstractDataRequest<R>> dataRequestBuilder, String accessToken) throws IOException, SpotifyWebApiException;
    <R> void execRequestAsync(Function<SpotifyApi, AbstractDataRequest<R>> dataRequestBuilder, String accessToken) throws IOException, SpotifyWebApiException;

    //-------------------------------
    <R> void execRequestListSync(RequestFunction<SpotifyApi, String, AbstractDataRequest<R>> dataRequestBuilder, HashMap<String, String> tokenDeviceMap) throws IOException, SpotifyWebApiException;
    <R> void execRequestListAsync(RequestFunction<SpotifyApi, String, AbstractDataRequest<R>> dataRequestBuilder, HashMap<String, String> tokenDeviceMap) throws IOException, SpotifyWebApiException;

}
