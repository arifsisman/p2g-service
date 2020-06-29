package vip.yazilim.p2g.web.enums;

import java.util.HashMap;

public enum FriendRequestStatus {
    WAITING("waiting"),
    ACCEPTED("accepted"),
    REJECTED("rejected"),
    IGNORED("ignored");

    private static final HashMap<String, FriendRequestStatus> map = new HashMap<>();

    static {
        for (FriendRequestStatus status : values()) {
            map.put(status.friendRequestStatus, status);
        }
    }

    private final String friendRequestStatus;

    FriendRequestStatus(String friendRequestStatus) {
        this.friendRequestStatus = friendRequestStatus;
    }

    public static Object keyOf(String type) {
        return map.get(type);
    }

    public String getFriendRequestStatus() {
        return this.friendRequestStatus;
    }
}
