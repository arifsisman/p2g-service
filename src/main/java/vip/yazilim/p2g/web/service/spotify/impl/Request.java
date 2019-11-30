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
import vip.yazilim.p2g.web.service.spotify.IRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class Request implements IRequest {

    private final Logger LOGGER = LoggerFactory.getLogger(Request.class);

    @Autowired
    @Qualifier(Constants.BEAN_NAME_CLIENT_CREDENTIALS)
    private SpotifyApi spotifyApi;

    @Override
    public List<AbstractDataRequest> initRequestList(List<SpotifyToken> spotifyTokenList, ARequestBuilder request) {

        List<AbstractDataRequest> requestList = new ArrayList<>();
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
    public void execRequestListAsync(List<AbstractDataRequest> requestList) {
        requestList.forEach(AbstractRequest::executeAsync);
    }

    @Override
    public void execRequestListSync(List<AbstractDataRequest> requestList) {
        for (AbstractDataRequest request : requestList) {
            try {
                request.execute();
            } catch (IOException | SpotifyWebApiException e) {
                LOGGER.error("An error occurred while executing request.");
            }
        }
    }

    @Override
    public AbstractDataRequest initRequest(ARequestBuilder request, SpotifyToken token) {
        SpotifyApi spotifyApi = initAuthorizedApi(token);

        return request.build(spotifyApi);
    }

    @Override
    public AbstractDataRequest initRequest(ARequestBuilder request) {
        return request.build(spotifyApi);
    }

    @Override
    public Object execRequestSync(AbstractDataRequest request) {
        try {
            return request.execute();
        } catch (IOException | SpotifyWebApiException e) {
            LOGGER.error("An error occurred while executing request.");
        }
        return null;
    }

    @Override
    public Object execRequestAsync(AbstractDataRequest request) {
        CompletableFuture result = request.executeAsync();
        return result.join();
    }

    @Override
    public SpotifyApi initAuthorizedApi(SpotifyToken token) {
        return new SpotifyApi.Builder()
                .setAccessToken(token.getAccessToken())
                .build();
    }

    @Override
    public SpotifyApi getClientCredentialsApi() {
        return spotifyApi;
    }

}
