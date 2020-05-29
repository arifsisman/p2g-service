package vip.yazilim.p2g.web.constant;

public final class Constants {

    /**
     * Utility class
     */
    private Constants() {
    }

    public static final String API = "/api";
    public static final String API_P2G = API;
    public static final String API_SPOTIFY = API + "/spotify";

    public static final String TABLE_PREFIX = "p2g_";

    public static final Integer WEBSOCKET_THREAD_POOL_SIZE = 100;
    public static final Integer WEBSOCKET_THREAD_POOL_SIZE_MAX = 1000;

    public static final Integer ROOM_SONG_LIMIT = 50;

}
