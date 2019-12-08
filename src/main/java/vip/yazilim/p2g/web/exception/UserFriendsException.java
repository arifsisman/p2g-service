package vip.yazilim.p2g.web.exception;

import vip.yazilim.spring.core.exception.general.GeneralException;

/**
 * @author mustafaarifsisman - 26.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public class UserFriendsException extends GeneralException {
    public UserFriendsException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public UserFriendsException(String message) {
        super(message);
    }
}
