package vip.yazilim.p2g.web.service.spotify.impl;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.requests.data.AbstractDataRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.constant.Constants;
import vip.yazilim.p2g.web.exception.SpotifyException;
import vip.yazilim.p2g.web.service.spotify.ISpotifyRequestService;
import vip.yazilim.p2g.web.service.spotify.model.PlayerModel;
import vip.yazilim.p2g.web.service.spotify.model.RequestFunction;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Iterator;
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

    @Autowired
    @Qualifier(Constants.BEAN_NAME_CLIENT_CREDENTIALS)
    private SpotifyApi spotifyApi;

    @Override
    public SpotifyApi initAuthorizedApi(String accessToken) {
        return new SpotifyApi.Builder()
                .setAccessToken(accessToken)
                .build();
    }

    //------------------------------------------------------
    @Override
    public <R> R execRequestSync(Function<SpotifyApi, AbstractDataRequest<R>> dataRequestBuilder) throws SpotifyException {
        return execRequest(dataRequestBuilder.apply(spotifyApi), false);
    }

    @Override
    public <R> void execRequestAsync(Function<SpotifyApi, AbstractDataRequest<R>> dataRequestBuilder) throws SpotifyException {
        execRequest(dataRequestBuilder.apply(spotifyApi), true);
    }

    //////////////////////////////////////////
    // Token
    //////////////////////////////////////////

    //------------------------------------------------------
    @Override
    public <R> R execRequestSync(Function<SpotifyApi, AbstractDataRequest<R>> dataRequestBuilder, String accessToken) throws SpotifyException {
        SpotifyApi spotifyApi = initAuthorizedApi(accessToken);
        return execRequest(dataRequestBuilder.apply(spotifyApi), false);
    }

    @Override
    public <R> void execRequestAsync(Function<SpotifyApi, AbstractDataRequest<R>> dataRequestBuilder, String accessToken) throws SpotifyException {
        SpotifyApi spotifyApi = initAuthorizedApi(accessToken);
        execRequest(dataRequestBuilder.apply(spotifyApi), true);
    }

    //------------------------------------------------------
    @Override
    public <R> void execRequestListSync(RequestFunction<SpotifyApi, String, AbstractDataRequest<R>> dataRequestBuilder, PlayerModel playerModel) throws SpotifyException {
        execRequestList(dataRequestBuilder, playerModel, false);
    }

    @Override
    public <R> void execRequestListAsync(RequestFunction<SpotifyApi, String, AbstractDataRequest<R>> dataRequestBuilder, PlayerModel playerModel) throws SpotifyException {
        execRequestList(dataRequestBuilder, playerModel, true);
    }

    private <R> void execRequestList(RequestFunction<SpotifyApi, String, AbstractDataRequest<R>> dataRequestBuilder, PlayerModel playerModel, boolean async) throws SpotifyException {
        List<AbstractDataRequest<R>> abstractDataRequests = new LinkedList<>();

        List<String> spotifyTokenList = playerModel.getSpotifyTokenList();
        List<String> userDeviceList = playerModel.getUserDeviceList();

        Iterator<String> tokenIterator = spotifyTokenList.iterator();
        Iterator<String> deviceOperator = userDeviceList.iterator();

        // create requests
        while (tokenIterator.hasNext() && deviceOperator.hasNext()) {
            String s = tokenIterator.next();
            String d = deviceOperator.next();
            SpotifyApi spotifyApi = initAuthorizedApi(s);
            abstractDataRequests.add(dataRequestBuilder.apply(spotifyApi, d));
        }

        // execute requests
        for (AbstractDataRequest<R> r : abstractDataRequests) {
            execRequest(r, async);
        }
    }

    //------------------------------------------------------
    private <R> R execRequest(AbstractDataRequest<R> abstractDataRequest, boolean async) throws SpotifyException {
        if (async) {
            abstractDataRequest.executeAsync();
            return null;
        } else {
            try {
                return abstractDataRequest.execute();
            } catch (IOException | SpotifyWebApiException e) {
                throw new SpotifyException(e.getCause().getMessage(), e);
        }
        }
    }
}
