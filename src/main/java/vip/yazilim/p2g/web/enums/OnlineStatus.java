package vip.yazilim.p2g.web.enums;

import java.util.HashMap;

public enum OnlineStatus {
    ONLINE("online"),
    OFFLINE("offline"),
    AWAY("away");

    private static final HashMap<String, OnlineStatus> map = new HashMap<>();

    static {
        for (OnlineStatus status : values()) {
            map.put(status.onlineStatus, status);
        }
    }

    private final String onlineStatus;

    OnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public static Object keyOf(String type) {
        return map.get(type);
    }

    public String getOnlineStatus() {
        return this.onlineStatus;
    }

}
