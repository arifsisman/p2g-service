package vip.yazilim.p2g.web.service.spotify.model;

/**
 * @author mustafaarifsisman - 1.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@FunctionalInterface
public interface RequestFunction<S, String, R> {
    R apply(S spotifyApi, String device);
}
