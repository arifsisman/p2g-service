package vip.yazilim.p2g.web.exception;

import vip.yazilim.spring.core.exception.GeneralException;

/**
 * @author mustafaarifsisman - 26.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public class RoomException extends GeneralException {

    public RoomException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public RoomException(String message) {
        super(message);
    }
}
