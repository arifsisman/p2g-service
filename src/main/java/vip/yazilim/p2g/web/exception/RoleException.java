package vip.yazilim.p2g.web.exception;

import vip.yazilim.spring.utils.exception.GeneralException;

/**
 * @author mustafaarifsisman - 3.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public class RoleException extends GeneralException {

    public RoleException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public RoleException(String message) {
        super(message);
    }
}
