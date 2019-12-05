package vip.yazilim.p2g.web.exception;


import vip.yazilim.spring.core.exception.GeneralException;
import vip.yazilim.spring.core.exception.database.DatabaseException;
import vip.yazilim.spring.core.exception.web.ServiceException;

/**
 * @author mustafaarifsisman - 1.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
public class RequestException extends ServiceException {

    public RequestException(String message, Exception exception) {
        super(message, exception);
    }

    public RequestException(Exception exception) {
        super(exception);
    }

    public RequestException(DatabaseException databaseException) {
        super(databaseException);
    }

    public RequestException(GeneralException generalException) {
        super(generalException);
    }

    public RequestException(String message) {
        super(message);
    }
}
