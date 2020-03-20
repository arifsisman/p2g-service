package vip.yazilim.p2g.web.enums;

/**
 * @author mustafaarifsisman - 14.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
public enum WebSocketDestinations {
    USER_INVITE("invites");

    private final String destination;

    WebSocketDestinations(String destination) {
        this.destination = destination;
    }

    public String getDestination() {
        return this.destination;
    }
}
