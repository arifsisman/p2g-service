package vip.yazilim.p2g.web.exception;

/**
 * @author mustafaarifsisman - 16.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String reason) {
        super(reason);
    }
}
