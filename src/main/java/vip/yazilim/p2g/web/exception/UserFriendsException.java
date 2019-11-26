package vip.yazilim.p2g.web.exception;

import vip.yazilim.spring.utils.exception.runtime.ServiceException;

/**
 * @author mustafaarifsisman - 26.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public class UserFriendsException extends ServiceException {
    public UserFriendsException(String message, Throwable throwable) {
        super(message, (Exception) throwable);
    }

    public UserFriendsException(String message) {
        super(message);
    }
}
