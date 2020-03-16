package vip.yazilim.p2g.web.service.spotify;

import vip.yazilim.p2g.web.entity.RoomUser;
import vip.yazilim.p2g.web.entity.Song;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ISpotifyPlayerService {

    boolean roomPlay(Song song, int ms, boolean checkCurrent);

    boolean roomPlayPause(Long roomId);

    boolean roomNext(Long roomId);

    boolean roomPrevious(Long roomId);

    boolean roomSeek(Long roomId, Integer ms);

    boolean roomRepeat(Long roomId);

    boolean userSyncWithRoom(RoomUser roomUser);

    boolean roomStop(Long roomId);

    boolean userDeSyncWithRoom(RoomUser roomUser);

}
