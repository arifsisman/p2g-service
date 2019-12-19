package vip.yazilim.p2g.web.exception;

/**
 * @author mustafaarifsisman - 19.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
public class AccountException extends RuntimeException {
    public AccountException(String reason) {
        super(reason);
    }
}
