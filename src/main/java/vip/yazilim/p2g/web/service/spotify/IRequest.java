package vip.yazilim.p2g.web.service.spotify;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.requests.data.AbstractDataRequest;
import vip.yazilim.p2g.web.entity.SpotifyToken;

import java.util.List;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IRequest {

    SpotifyApi initAuthorizedApi(SpotifyToken token);
    SpotifyApi getClientCredentialsApi();

    <R> AbstractDataRequest<R> initRequest(ARequestBuilder<R> request, SpotifyToken token);
    <R> AbstractDataRequest<R> initRequest(ARequestBuilder<R> request);
    <R> List<AbstractDataRequest<R>> initRequestList(ARequestBuilder<R> request, List<SpotifyToken> spotifyTokenList);

    <R> R execRequestSync(AbstractDataRequest<R> request);
    <R> R execRequestAsync(AbstractDataRequest<R> request);

    <R> void execRequestListSync(List<AbstractDataRequest<R>> requestList);
    <R> void execRequestListAsync(List<AbstractDataRequest<R>> requestList);

}
