package vip.yazilim.p2g.web.exception;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public class DatabaseException extends Exception {

    public DatabaseException(String message){
        super(message);
    }

    public DatabaseException(String message, Throwable throwable){
        super(message, throwable);
    }

}
