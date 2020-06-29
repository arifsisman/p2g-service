package vip.yazilim.p2g.web.service.spotify;

import vip.yazilim.p2g.web.entity.Song;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IPlayerService {

    boolean roomPlay(Song song, int ms, boolean checkCurrent);

    boolean roomPlayPause(Long roomId);

    boolean roomNext(Long roomId);

    boolean roomNext(Song song);

    boolean roomPrevious(Long roomId);

    boolean roomSeek(Long roomId, int ms);

    boolean roomRepeat(Long roomId);

    boolean roomStop(Long roomId);

    boolean syncWithRoom(String userId);

    void desyncWithRoom(String userId);

}
