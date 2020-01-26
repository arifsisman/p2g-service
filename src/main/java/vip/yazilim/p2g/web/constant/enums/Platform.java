package vip.yazilim.p2g.web.constant.enums;

import java.util.HashMap;

/**
 * @author mustafaarifsisman - 4.01.2020
 * @contact mustafaarifsisman@gmail.com
 */
public enum Platform {
    SPOTIFY("spotify");

    private static final HashMap<String, Platform> map = new HashMap<>();
    public final String name;

    Platform(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    static {
        for (Platform platform : values()) {
            map.put(platform.name, platform);
        }
    }
}
