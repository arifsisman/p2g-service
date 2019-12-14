package vip.yazilim.p2g.web.constant;

import java.util.HashMap;

/**
 * @author mustafaarifsisman - 12.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
public enum Privileges {
    GET_ROOM("get_room"),
    JOIN_ROOM("join_room"),
    CREATE_ROOM("create_room"),
    DELETE_ROOM("delete_room"),
    INVITE_ROOM("invite_room"),
    LISTEN_SONG("listen_song"),
    CONTROL_SONG("control_song"),
    ADD_SONG("add_song");

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
