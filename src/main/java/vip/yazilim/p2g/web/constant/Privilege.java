package vip.yazilim.p2g.web.constant;

import java.util.HashMap;

/**
 * @author mustafaarifsisman - 12.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
public enum Privilege {
    UNDEFINED("undefined"),
    ROOM_GET("room_get"),
    ROOM_JOIN_AND_LEAVE("room_join"),
    ROOM_INVITE_REPLY("room_invite_reply"),
    ROOM_CREATE("room_create"),
    ROOM_DELETE("room_delete"),
    ROOM_UPDATE("room_update"),
    ROOM_INVITE("room_invite"),
    ROOM_MANAGE_ROLES("room_manage_roles"),
    SONG_GET("song_get"),
    SONG_VOTE("song_vote"),
    SONG_CONTROL("song_control"),
    SONG_UPDATE("song_update"),
    SONG_ADD_AND_REMOVE("song_add_and_remove");

    private final String privilegeName;

    Privilege(String privilegeName) {
        this.privilegeName = privilegeName;
    }

    public String getPrivilegeName() {
        return this.privilegeName;
    }

    private static final HashMap<String, Privilege> map = new HashMap<>();

    public static Privilege keyOf(String type) {
        return map.get(type);
    }

    static {
        for (Privilege privilege : values()) {
            map.put(privilege.getPrivilegeName(), privilege);
        }
    }
}
