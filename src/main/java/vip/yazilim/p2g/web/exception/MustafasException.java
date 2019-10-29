package vip.yazilim.p2g.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class MustafasException extends RuntimeException {

    public MustafasException(String message){
        super(message);
    }

    public MustafasException(String message, Throwable throwable){
        super(message, throwable);
    }
}
