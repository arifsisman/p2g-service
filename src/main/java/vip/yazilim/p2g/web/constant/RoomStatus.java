package vip.yazilim.p2g.web.constant;

import java.util.HashMap;

/**
 * @author mustafaarifsisman - 4.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
public enum RoomStatus {
    CLOSED("closed"),
    UPDATED("updated");

    private final String roomStatus;

    RoomStatus(String songStatus) {
        this.roomStatus = songStatus;
    }

    public String getRoomStatus() {
        return this.roomStatus;
    }

    private static final HashMap<String, RoomStatus> map = new HashMap<>();

    public static Object keyOf(String type) {
        return map.get(type);
    }

    static {
        for (RoomStatus status : values()) {
            map.put(status.roomStatus, status);
        }
    }
}
