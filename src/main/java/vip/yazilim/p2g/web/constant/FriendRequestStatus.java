package vip.yazilim.p2g.web.constant;

import java.util.HashMap;

public enum FriendRequestStatus {
    WAITING("WAITING"),
    ACCEPTED("ACCEPTED"),
    REJECTED("REJECTED"),
    IGNORED("IGNORED");

    private final String friendRequestStatus;

    FriendRequestStatus(String friendRequestStatus) {
        this.friendRequestStatus = friendRequestStatus;
    }

    public String getFriendRequestStatus() {
        return this.friendRequestStatus;
    }

    private static final HashMap<String, FriendRequestStatus> map = new HashMap<>();

    public static Object keyOf(String type) {
        return map.get(type);
    }

    static {
        for (FriendRequestStatus status : values()) {
            map.put(status.friendRequestStatus, status);
        }
    }
}
