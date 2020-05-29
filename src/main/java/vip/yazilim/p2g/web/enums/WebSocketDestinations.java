package vip.yazilim.p2g.web.enums;

/**
 * @author mustafaarifsisman - 14.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
public enum WebSocketDestinations {
    INVITES;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
