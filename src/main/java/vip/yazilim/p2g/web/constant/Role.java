package vip.yazilim.p2g.web.constant;

import java.util.HashMap;

public enum Role {
    UNDEFINED("undefined"),
    P2G_USER("p2g_user"),
    ROOM_USER("room_user"),
    ROOM_MODERATOR("room_moderator"),
    ROOM_ADMIN("room_admin"),
    ROOM_OWNER("room_owner");

    private static final HashMap<String, Role> map = new HashMap<>();
    public final String role;

    Role(String role) {
        this.role = role;
    }

    public static Role getRole(String role) {
        return map.get(role);
    }

    public String getRole() {
        return this.role;
    }

    static {
        for (Role role : values()) {
            map.put(role.role, role);
        }
    }
}

