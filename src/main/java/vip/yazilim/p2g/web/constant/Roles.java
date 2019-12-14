package vip.yazilim.p2g.web.constant;

import java.util.HashMap;

public enum Roles {
    P2G_USER("p2g_user"),
    ROOM_USER("room_user"),
    ROOM_MODERATOR("room_moderator"),
    ROOM_ADMIN("room_admin"),
    ROOM_OWNER("room_owner");

    private final String roleName;

    Roles(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return this.roleName;
    }

    private static final HashMap<String, Roles> map = new HashMap<>();

    public static Object keyOf(String type) {
        return map.get(type);
    }

    static {
        for (Roles roles : values()) {
            map.put(roles.roleName, roles);
        }
    }
}

