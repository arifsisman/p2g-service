package vip.yazilim.p2g.web.exception;

import java.security.GeneralSecurityException;

/**
 * @author mustafaarifsisman - 16.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
public class ForbiddenException extends GeneralSecurityException {
    public ForbiddenException(String message){ super(message);}
    public ForbiddenException(String message, Throwable cause){ super(message, cause);}
}
