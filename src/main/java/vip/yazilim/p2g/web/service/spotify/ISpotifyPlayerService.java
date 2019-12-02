package vip.yazilim.p2g.web.service.spotify;

import vip.yazilim.p2g.web.entity.relation.RoomQueue;
import vip.yazilim.p2g.web.exception.PlayerException;
import vip.yazilim.p2g.web.exception.QueueException;
import vip.yazilim.p2g.web.exception.RequestException;
import vip.yazilim.spring.utils.exception.DatabaseException;
import vip.yazilim.spring.utils.exception.InvalidUpdateException;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ISpotifyPlayerService {

    void play(RoomQueue roomQueue) throws RequestException, PlayerException, DatabaseException, QueueException, InvalidUpdateException;

    void resume(String roomUuid) throws RequestException, DatabaseException;

    void pause(String roomUuid) throws RequestException, DatabaseException, InvalidUpdateException;

    void next(String roomUuid) throws RequestException, DatabaseException, PlayerException, QueueException, InvalidUpdateException;

    void previous(String roomUuid) throws RequestException, DatabaseException, PlayerException, QueueException, InvalidUpdateException;

    void seek(String roomUuid, Integer ms) throws RequestException, DatabaseException;

    void repeat(String roomUuid) throws RequestException, DatabaseException;

}
