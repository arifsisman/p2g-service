package vip.yazilim.p2g.web.exception;

/**
 * @author mustafaarifsisman - 2.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public class InviteException extends RuntimeException {

    public InviteException(String message){
        super(message);
    }

    public InviteException(String message, Throwable throwable){
        super(message, throwable);
    }
}
