package vip.yazilim.p2g.web.enums;

import java.util.HashMap;

/**
 * @author mustafaarifsisman - 4.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
public enum RoomStatus {
    CLOSED("closed"),
    UPDATED("updated");

    private static final HashMap<String, RoomStatus> map = new HashMap<>();

    static {
        for (RoomStatus status : values()) {
            map.put(status.roomStatus, status);
        }
    }

    private final String roomStatus;

    RoomStatus(String songStatus) {
        this.roomStatus = songStatus;
    }

    public static Object keyOf(String type) {
        return map.get(type);
    }

    public String getRoomStatus() {
        return this.roomStatus;
    }
}
