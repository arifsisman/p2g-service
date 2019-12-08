package vip.yazilim.p2g.web.exception;

import vip.yazilim.spring.core.exception.general.GeneralException;

/**
 * @author mustafaarifsisman - 06.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
public class UserException extends GeneralException {
    public UserException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public UserException(String message) {
        super(message);
    }
}
