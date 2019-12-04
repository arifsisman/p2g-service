package vip.yazilim.p2g.web.exception;

import vip.yazilim.spring.utils.exception.GeneralException;

/**
 * @author mustafaarifsisman - 04.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
public class AccountException extends GeneralException {
    public AccountException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public AccountException(String message) {
        super(message);
    }
}
