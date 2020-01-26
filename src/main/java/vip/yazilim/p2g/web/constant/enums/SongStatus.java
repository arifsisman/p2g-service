package vip.yazilim.p2g.web.constant.enums;

import java.util.HashMap;

public enum SongStatus {
    PLAYED("played"),
    PLAYING("playing"),
    PAUSED("paused"),
    NEXT("next");

    private final String songStatus;

    SongStatus(String songStatus) {
        this.songStatus = songStatus;
    }

    public String getSongStatus() {
        return this.songStatus;
    }

    private static final HashMap<String, SongStatus> map = new HashMap<>();

    public static Object keyOf(String type) {
        return map.get(type);
    }

    static {
        for (SongStatus status : values()) {
            map.put(status.songStatus, status);
        }
    }
}
