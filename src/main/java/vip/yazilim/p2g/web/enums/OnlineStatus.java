package vip.yazilim.p2g.web.enums;

import java.util.HashMap;

public enum OnlineStatus {
    ONLINE("online"),
    OFFLINE("offline"),
    AWAY("away");

    private final String onlineStatus;

    OnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getOnlineStatus() {
        return this.onlineStatus;
    }

    private static final HashMap<String, OnlineStatus> map = new HashMap<>();

    public static Object keyOf(String type) {
        return map.get(type);
    }

    static {
        for (OnlineStatus status : values()) {
            map.put(status.onlineStatus, status);
        }
    }

}
