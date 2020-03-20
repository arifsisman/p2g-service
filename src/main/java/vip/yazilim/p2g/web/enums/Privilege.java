package vip.yazilim.p2g.web.enums;

import java.util.HashMap;

/**
 * @author mustafaarifsisman - 12.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
public enum Privilege {
    UNDEFINED("undefined"),
    ROOM_JOIN_AND_LEAVE("room_join"),
    ROOM_CREATE("room_create"),
    ROOM_UPDATE("room_update"),
    ROOM_INVITE_AND_REPLY("room_invite"),
    ROOM_MANAGE_ROLES("room_manage_roles"),
    SONG_SEARCH("song_search"),
    SONG_GET("song_get"),
    SONG_VOTE("song_vote"),
    SONG_CONTROL("song_control"),
    SONG_ADD_AND_REMOVE("song_add_and_remove"),
    ROOM_CLEAR_QUEUE("room_clear_queue");

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
