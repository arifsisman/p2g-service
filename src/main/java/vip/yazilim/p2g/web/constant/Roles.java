package vip.yazilim.p2g.web.constant;

public enum Roles {
    ADMIN("ADMIN"),
    MODERATOR("MODERATOR"),
    USER("USER");

    private final  String roleName;

    Roles(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }
}

