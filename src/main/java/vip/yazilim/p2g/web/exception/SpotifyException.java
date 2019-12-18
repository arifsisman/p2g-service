package vip.yazilim.p2g.web.exception;


import vip.yazilim.spring.core.exception.web.MsRuntimeException;

/**
 * @author mustafaarifsisman - 1.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
public class SpotifyException extends MsRuntimeException {

    public SpotifyException(String message, Exception exception) {
        super(message, exception);
    }

    public SpotifyException(String message) {
        super(message);
    }
}
