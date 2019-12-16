package vip.yazilim.p2g.web.constant;

import java.util.HashMap;

public enum Roles {
    UNDEFINED("undefined"),
    P2G_USER("p2g_user"),
    ROOM_USER("room_user"),
    ROOM_MODERATOR("room_moderator"),
    ROOM_ADMIN("room_admin"),
    ROOM_OWNER("room_owner");

    private static final HashMap<String, Roles> map = new HashMap<>();
    public final String roleName;

    Roles(String roleName) {
        this.roleName = roleName;
    }

    public static Roles keyOf(String type) {
        return map.get(type.toLowerCase());
    }

    public String getRoleName() {
        return this.roleName;
    }

    static {
        for (Roles roles : values()) {
            map.put(roles.roleName, roles);
        }
    }
}

