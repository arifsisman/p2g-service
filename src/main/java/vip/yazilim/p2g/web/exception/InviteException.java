package vip.yazilim.p2g.web.exception;

import vip.yazilim.spring.core.exception.general.GeneralException;

/**
 * @author mustafaarifsisman - 2.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public class InviteException extends GeneralException {

    public InviteException(String message){
        super(message);
    }

    public InviteException(String message, Throwable throwable){
        super(message, throwable);
    }
}
