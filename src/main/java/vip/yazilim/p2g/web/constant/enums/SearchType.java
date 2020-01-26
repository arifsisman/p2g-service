package vip.yazilim.p2g.web.constant.enums;

import java.util.HashMap;

/**
 * @author mustafaarifsisman - 29.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public enum SearchType {
    ALBUM("album"),
    PLAYLIST("playlist"),
    TRACK("track"),
    ARTIST("artist"),
    USER("user");

    private static final HashMap<String, SearchType> map = new HashMap<>();
    public final String type;

    SearchType(String type) {
        this.type = type;
    }

    public static Object keyOf(String type) {
        return map.get(type);
    }

    public String getType() {
        return this.type;
    }

    static {
        for (SearchType searchType : values()) {
            map.put(searchType.type, searchType);
        }
    }
}
