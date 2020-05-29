package vip.yazilim.p2g.web.enums;

/**
 * @author mustafaarifsisman - 29.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public enum SearchType {
    SONG("track"),
    ALBUM("album"),
    PLAYLIST("playlist");

    public final String type;

    SearchType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public static String getAllTypes() {
        return SONG.type + "," + ALBUM.type + "," + PLAYLIST.type;
    }
}
