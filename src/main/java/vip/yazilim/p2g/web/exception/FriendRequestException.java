package vip.yazilim.p2g.web.exception;

import vip.yazilim.spring.core.exception.general.GeneralException;

/**
 * @author mustafaarifsisman - 26.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public class FriendRequestException extends GeneralException {
    public FriendRequestException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public FriendRequestException(String message) {
        super(message);
    }
}
