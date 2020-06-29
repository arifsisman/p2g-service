package vip.yazilim.p2g.web.enums;

import java.util.HashMap;

public enum SongStatus {
    PLAYED("played"),
    PLAYING("playing"),
    PAUSED("paused"),
    NEXT("next");

    private static final HashMap<String, SongStatus> map = new HashMap<>();

    static {
        for (SongStatus status : values()) {
            map.put(status.songStatus, status);
        }
    }

    private final String songStatus;

    SongStatus(String songStatus) {
        this.songStatus = songStatus;
    }

    public static Object keyOf(String type) {
        return map.get(type);
    }

    public String getSongStatus() {
        return this.songStatus;
    }
}
