package vip.yazilim.p2g.web.constant;

import java.util.HashMap;

public enum QueueStatus {
    PLAYED("played"),
    PREVIOUS("previous"),
    PLAYING("playing"),
    PAUSED("paused"),
    NEXT("next");

    private final String queueStatus;

    QueueStatus(String queueStatus) {
        this.queueStatus = queueStatus;
    }

    public String getQueueStatus() {
        return this.queueStatus;
    }

    private static final HashMap<String, QueueStatus> map = new HashMap<>();

    public static Object keyOf(String type) {
        return map.get(type);
    }

    static {
        for (QueueStatus status : values()) {
            map.put(status.queueStatus, status);
        }
    }
}
