package vip.yazilim.p2g.web.exception;

import vip.yazilim.spring.utils.exception.GeneralException;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public class PlayerException extends GeneralException {

    public PlayerException(String message) {
        super(message);
    }

    public PlayerException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
