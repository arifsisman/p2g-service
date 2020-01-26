package vip.yazilim.p2g.web.service.spotify.impl;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.requests.data.AbstractDataRequest;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.service.spotify.ISpotifyRequestService;
import vip.yazilim.p2g.web.service.spotify.model.RequestFunction;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Transactional
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
    public <R> R execRequestSync(Function<SpotifyApi, AbstractDataRequest<R>> dataRequestBuilder, String accessToken) throws IOException, SpotifyWebApiException {
        SpotifyApi spotifyApi = initAuthorizedApi(accessToken);
        return execRequest(dataRequestBuilder.apply(spotifyApi), false);
    }

    @Override
    public <R> void execRequestAsync(Function<SpotifyApi, AbstractDataRequest<R>> dataRequestBuilder, String accessToken) throws IOException, SpotifyWebApiException {
        SpotifyApi spotifyApi = initAuthorizedApi(accessToken);
        execRequest(dataRequestBuilder.apply(spotifyApi), true);
    }

    //------------------------------------------------------
    @Override
    public <R> void execRequestListSync(RequestFunction<SpotifyApi, String, AbstractDataRequest<R>> dataRequestBuilder, HashMap<String, String> tokenDeviceMap) throws IOException, SpotifyWebApiException {
        execRequestList(dataRequestBuilder, tokenDeviceMap, false);
    }

    @Override
    public <R> void execRequestListAsync(RequestFunction<SpotifyApi, String, AbstractDataRequest<R>> dataRequestBuilder, HashMap<String, String> tokenDeviceMap) throws IOException, SpotifyWebApiException {
        execRequestList(dataRequestBuilder, tokenDeviceMap, true);
    }

    private <R> void execRequestList(RequestFunction<SpotifyApi, String, AbstractDataRequest<R>> dataRequestBuilder, HashMap<String, String> tokenDeviceMap, boolean async) throws IOException, SpotifyWebApiException {
        List<AbstractDataRequest<R>> abstractDataRequests = new LinkedList<>();

        // create requests
        tokenDeviceMap.forEach((token, device) -> abstractDataRequests.add(dataRequestBuilder.apply(initAuthorizedApi(token), device)));

        // execute requests
        for (AbstractDataRequest<R> r : abstractDataRequests) {
            execRequest(r, async);
        }
    }

    //------------------------------------------------------
    private <R> R execRequest(AbstractDataRequest<R> abstractDataRequest, boolean async) throws IOException, SpotifyWebApiException {
        if (async) {
            abstractDataRequest.executeAsync();
            return null;
        } else {
            return abstractDataRequest.execute();
        }
    }
}
