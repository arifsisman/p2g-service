package vip.yazilim.p2g.web.constant;

import java.util.HashMap;

/**
 * @author mustafaarifsisman - 12.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
public enum Privileges {
    ROOM_GET("room_get"),
    ROOM_JOIN("room_join"),
    ROOM_INVITE_REPLY("room_invite_reply"),
    ROOM_CREATE("room_create"),
    ROOM_DELETE("room_delete"),
    ROOM_INVITE("room_invite"),
    ROOM_MANAGE_ROLES("room_manage_roles"),
    SONG_LISTEN("song_listen"),
    SONG_CONTROL("song_control"),
    SONG_ADD("song_add");

    private final String privilegeName;

    Privileges(String privilegeName) {
        this.privilegeName = privilegeName;
    }

    public String getPrivilegeName() {
        return this.privilegeName;
    }

    private static final HashMap<String, Privileges> map = new HashMap<>();

    public static Object keyOf(String type) {
        return map.get(type);
    }

    static {
        for (Privileges privileges : values()) {
            map.put(privileges.getPrivilegeName(), privileges);
        }
    }
}
