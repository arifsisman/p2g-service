package vip.yazilim.p2g.web.constant;

import java.util.HashMap;

/**
 * @author mustafaarifsisman - 1.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
public enum SpotifyScopes {
    MODIFY_PLAYBACK("user-modify-playback-state"),
    READ_EMAIL("user-read-email"),
    READ_PLAYBACK("user-read-playback-state"),
    READ_PRIVATE("user-read-private");

    private final String scopeName;

    SpotifyScopes(String scopeName) {
        this.scopeName = scopeName;
    }

    public static String getAll() {
        StringBuilder stringBuilder = new StringBuilder();

        for (SpotifyScopes s : values())
            stringBuilder.append(s.scopeName).append(",");

        return stringBuilder.toString();
    }

    private static final HashMap<String, SpotifyScopes> map = new HashMap<>();

    public static Object keyOf(String type) {
        return map.get(type);
    }

    static {
        for (SpotifyScopes spotifyScopes : values()) {
            map.put(spotifyScopes.scopeName, spotifyScopes);
        }
    }


}
