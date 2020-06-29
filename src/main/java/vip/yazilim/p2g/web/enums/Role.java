package vip.yazilim.p2g.web.enums;

import java.util.HashMap;

public enum Role {
    UNDEFINED("Undefined"),
    P2G_USER("P2G_User"),
    ROOM_USER("User"),
    ROOM_DJ("DJ"),
    ROOM_ADMIN("Admin"),
    ROOM_OWNER("Owner");

    private static final HashMap<String, Role> map = new HashMap<>();

    static {
        for (Role role : values()) {
            map.put(role.role, role);
        }
    }

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
}

