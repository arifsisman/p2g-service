package vip.yazilim.p2g.web.exception;

import vip.yazilim.spring.utils.exception.runtime.ServiceException;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public class PlayerException extends ServiceException {

    public PlayerException(String message) {
        super(message);
    }

    public PlayerException(String message, Throwable throwable) {
        super(message, (Exception) throwable);
    }

}
