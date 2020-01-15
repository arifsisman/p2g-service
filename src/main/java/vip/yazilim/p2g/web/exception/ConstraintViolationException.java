package vip.yazilim.p2g.web.exception;

/**
 * @author mustafaarifsisman - 14.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
public class ConstraintViolationException extends RuntimeException {
    public ConstraintViolationException(String reason) {
        super(reason);
    }
}
