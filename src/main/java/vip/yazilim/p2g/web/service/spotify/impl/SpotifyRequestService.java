package vip.yazilim.p2g.web.service.spotify.impl;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.requests.data.AbstractDataRequest;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.service.spotify.ISpotifyRequestService;
import vip.yazilim.p2g.web.service.spotify.model.RequestFunction;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class SpotifyRequestService implements ISpotifyRequestService {

    @Override
    public SpotifyApi initAuthorizedApi(String accessToken) {
        return new SpotifyApi.Builder()
                .setAccessToken(accessToken)
                .build();
    }

    //////////////////////////////////////////
    // Token
    //////////////////////////////////////////

    //------------------------------------------------------
    @Override
    public <R> R execRequestSync(Function<SpotifyApi, AbstractDataRequest<R>> dataRequestBuilder, String accessToken) {
        SpotifyApi spotifyApi = initAuthorizedApi(accessToken);
        return execRequest(dataRequestBuilder.apply(spotifyApi), false);
    }

    @Override
    public <R> void execRequestAsync(Function<SpotifyApi, AbstractDataRequest<R>> dataRequestBuilder, String accessToken) {
        SpotifyApi spotifyApi = initAuthorizedApi(accessToken);
        execRequest(dataRequestBuilder.apply(spotifyApi), true);
    }

    //------------------------------------------------------
    @Override
    public <R> void execRequestListSync(RequestFunction<SpotifyApi, String, AbstractDataRequest<R>> dataRequestBuilder, Map<String, String> tokenDeviceMap) {
        execRequestList(dataRequestBuilder, tokenDeviceMap, false);
    }

    @Override
    public <R> void execRequestListAsync(RequestFunction<SpotifyApi, String, AbstractDataRequest<R>> dataRequestBuilder, Map<String, String> tokenDeviceMap) {
        execRequestList(dataRequestBuilder, tokenDeviceMap, true);
    }

    private <R> void execRequestList(RequestFunction<SpotifyApi, String, AbstractDataRequest<R>> dataRequestBuilder, Map<String, String> tokenDeviceMap, boolean async) {
        List<AbstractDataRequest<R>> abstractDataRequests = new LinkedList<>();

        // create requests
        tokenDeviceMap.forEach((token, device) -> abstractDataRequests.add(dataRequestBuilder.apply(initAuthorizedApi(token), device)));

        // execute requests
        for (AbstractDataRequest<R> r : abstractDataRequests) {
            execRequest(r, async);
        }
    }

    //------------------------------------------------------
    @SneakyThrows
    private <R> R execRequest(AbstractDataRequest<R> abstractDataRequest, boolean async) {
        if (async) {
            abstractDataRequest.executeAsync();
            return null;
        } else {
            return abstractDataRequest.execute();
        }
    }
}
