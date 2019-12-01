package vip.yazilim.p2g.web.exception;

import vip.yazilim.spring.utils.exception.GeneralException;

/**
 * @author mustafaarifsisman - 1.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
public class RequestException extends GeneralException {
    public RequestException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public RequestException(String message) {
        super(message);
    }
}
