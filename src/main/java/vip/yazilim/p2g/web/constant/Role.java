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
    public final String roleName;

    Role(String roleName) {
        this.roleName = roleName;
    }

    public static Role getRole(String roleName) {
        return map.get(roleName);
    }

    public String getRoleName() {
        return this.roleName;
    }

    static {
        for (Role role : values()) {
            map.put(role.roleName, role);
        }
    }
}

