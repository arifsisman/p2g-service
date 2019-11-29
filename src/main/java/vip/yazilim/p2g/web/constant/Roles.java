package vip.yazilim.p2g.web.constant;

import java.util.HashMap;

public enum Roles {
    ADMIN("ADMIN"),
    MODERATOR("MODERATOR"),
    USER("USER");

    private final  String roleName;

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

