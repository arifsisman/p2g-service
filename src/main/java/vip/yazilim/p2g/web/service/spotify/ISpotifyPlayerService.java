package vip.yazilim.p2g.web.service.spotify;

import vip.yazilim.p2g.web.exception.PlayerException;
import vip.yazilim.p2g.web.exception.RequestException;
import vip.yazilim.spring.utils.exception.DatabaseException;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ISpotifyPlayerService {

    void play(String roomUuid, String songUri) throws RequestException, PlayerException, DatabaseException;

    void play(String roomUuid) throws RequestException, DatabaseException;

    void pause(String roomUuid) throws RequestException, DatabaseException;

    void next(String roomUuid) throws RequestException, DatabaseException;

    void previous(String roomUuid) throws RequestException, DatabaseException;

    void seek(String roomUuid, Integer ms) throws RequestException, DatabaseException;

    void repeat(String roomUuid) throws RequestException, DatabaseException;

}
