package vip.yazilim.p2g.web.service.spotify;

/**
 * @author mustafaarifsisman - 1.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@FunctionalInterface
public interface RFunction<S, String, R> {
    R apply(S spotifyApi, String device);
}
