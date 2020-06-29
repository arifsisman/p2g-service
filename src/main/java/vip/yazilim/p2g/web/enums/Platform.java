package vip.yazilim.p2g.web.enums;

import java.util.HashMap;

/**
 * @author mustafaarifsisman - 4.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
public enum Platform {
    SPOTIFY("spotify");

    private static final HashMap<String, Platform> map = new HashMap<>();

    static {
        for (Platform platform : values()) {
            map.put(platform.name, platform);
        }
    }

    public final String name;

    Platform(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
