package vip.yazilim.p2g.web.util;

import vip.yazilim.p2g.web.constant.enums.SongStatus;
import vip.yazilim.p2g.web.entity.Song;

import java.util.List;

/**
 * @author mustafaarifsisman - 08.03.2020
 * @contact mustafaarifsisman@gmail.com
 */
public class RoomHelper {
    public static Song getRoomCurrentSong(List<Song> songList) {
        if (songList.isEmpty()) {
            return null;
        } else {
            Song playing = null;
            Song paused = null;
            Song next = null;

            for (Song s : songList) {
                if (s.getSongStatus().equals(SongStatus.PLAYING.getSongStatus())) {
                    playing = s;
                } else if (s.getSongStatus().equals(SongStatus.PAUSED.getSongStatus())) {
                    paused = s;
                } else if (s.getSongStatus().equals(SongStatus.NEXT.getSongStatus())) {
                    if (next == null) {
                        next = s;
                    }
                }
            }

            if (playing != null) {
                return playing;
            } else if (paused != null) {
                return paused;
            } else return next;
        }
    }

    public static String getQueuedSongNames(List<Song> songs) {
        StringBuilder stringBuilder = new StringBuilder();

        for (Song s : songs) {
            stringBuilder.append("\n").append(s);
        }

        return stringBuilder.toString();
    }
}
