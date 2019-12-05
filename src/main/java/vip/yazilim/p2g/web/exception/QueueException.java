package vip.yazilim.p2g.web.exception;

import vip.yazilim.spring.core.exception.GeneralException;

/**
 * @author mustafaarifsisman - 02.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
public class QueueException extends GeneralException {
    public QueueException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public QueueException(String message) {
        super(message);
    }
}
