package vip.yazilim.p2g.web.exception;

import vip.yazilim.spring.utils.exception.runtime.ServiceException;

/**
 * @author mustafaarifsisman - 26.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public class TokenException extends ServiceException {

    public TokenException(String message) {
        super(message);
    }

    public TokenException(String message, Throwable throwable) {
        super(message, (Exception) throwable);
    }
}
