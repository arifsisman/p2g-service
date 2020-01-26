package vip.yazilim.p2g.web.constant.enums;

import java.util.HashMap;

public enum FriendRequestStatus {
    WAITING("waiting"),
    ACCEPTED("accepted"),
    REJECTED("rejected"),
    IGNORED("ignored");

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
