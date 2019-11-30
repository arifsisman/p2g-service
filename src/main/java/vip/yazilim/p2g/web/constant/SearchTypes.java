package vip.yazilim.p2g.web.constant;

import java.util.HashMap;

/**
 * @author mustafaarifsisman - 29.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public enum SearchTypes {
    ALBUM("ALBUM"),
    PLAYLIST("PLAYLIST"),
    TRACK("TRACK"),
    ARTIST("ARTIST");

    private static final HashMap<String, SearchTypes> map = new HashMap<>();
    public final String type;

    SearchTypes(String type) {
        this.type = type;
    }

    public static Object keyOf(String type) {
        return map.get(type);
    }

    public String getType() {
        return this.type;
    }

    static {
        for (SearchTypes searchTypes : values()) {
            map.put(searchTypes.type, searchTypes);
        }
    }
}
