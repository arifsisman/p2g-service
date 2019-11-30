package vip.yazilim.p2g.web.service.spotify.impl;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.requests.AbstractRequest;
import com.wrapper.spotify.requests.data.AbstractDataRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.constant.Constants;
import vip.yazilim.p2g.web.entity.SpotifyToken;
import vip.yazilim.p2g.web.service.spotify.ARequestBuilder;
import vip.yazilim.p2g.web.service.spotify.ISRequestService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class SRequestService implements ISRequestService {

    private final Logger LOGGER = LoggerFactory.getLogger(SRequestService.class);

    @Autowired
    @Qualifier(Constants.BEAN_NAME_CLIENT_CREDENTIALS)
    private SpotifyApi spotifyApi;

    @Override
    public SpotifyApi initAuthorizedApi(SpotifyToken token) {
        return new SpotifyApi.Builder()
                .setAccessToken(token.getAccessToken())
                .build();
    }


    private <R> AbstractDataRequest<R> initRequest(ARequestBuilder<R> request, SpotifyToken token) {
        SpotifyApi spotifyApi = initAuthorizedApi(token);

        return request.build(spotifyApi);
    }

//    private <R> AbstractDataRequest<R> initRequest(SpotifyToken token, Function<SpotifyApi, AbstractDataRequest<R>> dataRequestBuilder) {
//        SpotifyApi spotifyApi = initAuthorizedApi(token);
//        return dataRequestBuilder.apply(spotifyApi);
//    }

    @Override
    public <R> R execRequest(ARequestBuilder<R> request, boolean async) {
        AbstractDataRequest<R> abstractDataRequest = request.build(spotifyApi);

        if (async) {
            return abstractDataRequest.executeAsync().join();
        } else {
            try {
                return abstractDataRequest.execute();
            } catch (IOException | SpotifyWebApiException e) {
                LOGGER.error("An error occurred while executing request.");
            }
        }
        return null;
    }

    @Override
    public <R> R execRequest(ARequestBuilder<R> request, SpotifyToken token, boolean async) {
        AbstractDataRequest<R> abstractDataRequest = initRequest(request, token);

        if (async) {
            return abstractDataRequest.executeAsync().join();
        } else {
            try {
                return abstractDataRequest.execute();
            } catch (IOException | SpotifyWebApiException e) {
                LOGGER.error("An error occurred while executing request.");
            }
        }
        return null;
    }

    private <R> List<AbstractDataRequest<R>> initRequestList(ARequestBuilder<R> request, List<SpotifyToken> spotifyTokenList) {
        List<AbstractDataRequest<R>> requestList = new ArrayList<>();
        SpotifyApi spotifyApi;

        for (SpotifyToken token : spotifyTokenList) {
            spotifyApi = new SpotifyApi.Builder()
                    .setAccessToken(token.getAccessToken())
                    .build();

            requestList.add(request.build(spotifyApi));
        }

        return requestList;
    }

    @Override
    public <R> void execRequestList(ARequestBuilder<R> request, List<SpotifyToken> spotifyTokenList, boolean async) {
        List<AbstractDataRequest<R>> abstractDataRequests = initRequestList(request, spotifyTokenList);
        if (async) {
            abstractDataRequests.forEach(AbstractRequest::executeAsync);
        } else {
            for (AbstractDataRequest r : abstractDataRequests) {
                try {
                    r.execute();
                } catch (IOException | SpotifyWebApiException e) {
                    LOGGER.error("An error occurred while executing request.");
                }
            }
        }

    }

}
