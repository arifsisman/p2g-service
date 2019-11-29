package vip.yazilim.p2g.web.spotify.impl;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.requests.AbstractRequest;
import com.wrapper.spotify.requests.data.AbstractDataRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.SpotifyToken;
import vip.yazilim.p2g.web.spotify.ARequest;
import vip.yazilim.p2g.web.spotify.IRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class Request implements IRequest {

    private final Logger LOGGER = LoggerFactory.getLogger(Request.class);

    @Override
    public List<AbstractDataRequest> initRequestList(List<SpotifyToken> spotifyTokenList, ARequest request) {

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
    public AbstractDataRequest initRequest(SpotifyToken token, ARequest request) {
        AbstractDataRequest dataRequest;

        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setAccessToken(token.getAccessToken())
                .build();

        return request.build(spotifyApi);
    }


    @Override
    public Object execRequest(AbstractDataRequest request) {
        Object var = new Object();

        try {
            var = request.execute();
        } catch (IOException | SpotifyWebApiException e) {
            e.printStackTrace();
        }

        return var;
    }

}
