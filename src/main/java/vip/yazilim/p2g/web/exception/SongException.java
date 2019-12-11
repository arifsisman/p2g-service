package vip.yazilim.p2g.web.exception;

import vip.yazilim.spring.core.exception.general.GeneralException;

/**
 * @author mustafaarifsisman - 02.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
public class SongException extends GeneralException {
    public SongException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public SongException(String message) {
        super(message);
    }
}
