package vip.yazilim.p2g.web.enums;

public enum Role {
    UNDEFINED("Undefined"),
    P2G_USER("P2G_User"),
    ROOM_USER("User"),
    ROOM_DJ("DJ"),
    ROOM_ADMIN("Admin"),
    ROOM_OWNER("Owner");

    public final String roleName;

    Role(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return this.roleName;
    }
}

