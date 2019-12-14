package vip.yazilim.p2g.web.constant;

import java.util.LinkedList;
import java.util.ListIterator;

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

    private static final LinkedList<String> list = new LinkedList<>();

    public static String getNextOf(String roleName) {
        int index = list.indexOf(roleName);
        ListIterator iterator = list.listIterator(index);

        String nextRole = iterator.next().toString();
        return (nextRole.equals(ROOM_OWNER.roleName)) ? ROOM_ADMIN.roleName : nextRole;
    }

    public static String getPreviousOf(String roleName) {
        int index = list.indexOf(roleName);
        ListIterator iterator = list.listIterator(index);

        String previousRole = iterator.next().toString();
        return (previousRole.equals(P2G_USER.roleName)) ? ROOM_USER.roleName : previousRole;
    }

    static {
        for (Roles roles : values()) {
            list.add(roles.roleName);
        }
    }
}

