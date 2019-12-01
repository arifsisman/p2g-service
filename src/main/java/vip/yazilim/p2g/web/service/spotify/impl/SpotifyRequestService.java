package vip.yazilim.p2g.web.service.spotify.impl;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.requests.data.AbstractDataRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.constant.Constants;
import vip.yazilim.p2g.web.entity.SpotifyToken;
import vip.yazilim.p2g.web.exception.RequestException;
import vip.yazilim.p2g.web.service.spotify.ISpotifyRequestService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class SpotifyRequestService implements ISpotifyRequestService {

    private final Logger LOGGER = LoggerFactory.getLogger(SpotifyRequestService.class);

    @Autowired
    @Qualifier(Constants.BEAN_NAME_CLIENT_CREDENTIALS)
    private SpotifyApi spotifyApi;

    @Override
    public SpotifyApi initAuthorizedApi(SpotifyToken token) {
        return new SpotifyApi.Builder()
                .setAccessToken(token.getAccessToken())
                .build();
    }

    //------------------------------------------------------
    @Override
    public <R> R execRequestSync(Function<SpotifyApi, AbstractDataRequest<R>> dataRequestBuilder) throws RequestException {
        return execRequest(dataRequestBuilder.apply(spotifyApi), false);
    }

    @Override
    public <R> R execRequestAsync(Function<SpotifyApi, AbstractDataRequest<R>> dataRequestBuilder) throws RequestException {
        return execRequest(dataRequestBuilder.apply(spotifyApi), true);
    }

    //////////////////////////////////////////
    // Token
    //////////////////////////////////////////

    //------------------------------------------------------
    @Override
    public <R> R execRequestSync(Function<SpotifyApi, AbstractDataRequest<R>> dataRequestBuilder, SpotifyToken token) throws RequestException {
        SpotifyApi spotifyApi = initAuthorizedApi(token);
        return execRequest(dataRequestBuilder.apply(spotifyApi), false);
    }

    @Override
    public <R> R execRequestAsync(Function<SpotifyApi, AbstractDataRequest<R>> dataRequestBuilder, SpotifyToken token) throws RequestException {
        SpotifyApi spotifyApi = initAuthorizedApi(token);
        return execRequest(dataRequestBuilder.apply(spotifyApi), true);
    }

    //------------------------------------------------------
    @Override
    public <R> void execRequestListSync(Function<SpotifyApi, AbstractDataRequest<R>> dataRequestBuilder, List<SpotifyToken> spotifyTokenList) throws RequestException {
        execRequestList(dataRequestBuilder, spotifyTokenList, false);
    }

    @Override
    public <R> void execRequestListAsync(Function<SpotifyApi, AbstractDataRequest<R>> dataRequestBuilder, List<SpotifyToken> spotifyTokenList) throws RequestException {
        execRequestList(dataRequestBuilder, spotifyTokenList, true);
    }

    private <R> void execRequestList(Function<SpotifyApi, AbstractDataRequest<R>> dataRequestBuilder, List<SpotifyToken> spotifyTokenList, boolean async) throws RequestException {
        List<AbstractDataRequest<R>> abstractDataRequests = new LinkedList<>();

        for (SpotifyToken token : spotifyTokenList) {
            SpotifyApi spotifyApi = initAuthorizedApi(token);
            abstractDataRequests.add(dataRequestBuilder.apply(spotifyApi));
        }

        for (AbstractDataRequest<R> r : abstractDataRequests) {
            execRequest(r, async);
        }
    }

    //------------------------------------------------------
    private <R> R execRequest(AbstractDataRequest<R> abstractDataRequest, boolean async) throws RequestException {
        if (async) {
            return abstractDataRequest.executeAsync().join();
        } else {
            try {
                return abstractDataRequest.execute();
            } catch (IOException | SpotifyWebApiException e) {
                throw new RequestException("An error occurred while executing request.", e);
            }
        }
    }
}
