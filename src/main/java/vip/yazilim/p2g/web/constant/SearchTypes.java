package vip.yazilim.p2g.web.constant;

import java.util.HashMap;

/**
 * @author mustafaarifsisman - 29.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public enum SearchTypes {
    ALBUM("album"),
    PLAYLIST("playlist"),
    TRACK("track");

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
        SearchTypes[] var0 = values();

        for (SearchTypes searchTypes : var0) {
            map.put(searchTypes.type, searchTypes);
        }
    }
}
