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

    void resume(RoomQueue roomQueue) throws RequestException, DatabaseException;

    void pause(RoomQueue roomQueue) throws RequestException, DatabaseException, InvalidUpdateException;

    void next(RoomQueue roomQueue) throws RequestException, DatabaseException, PlayerException, QueueException, InvalidUpdateException;

    void previous(RoomQueue roomQueue) throws RequestException, DatabaseException, PlayerException, QueueException, InvalidUpdateException;

    void seek(RoomQueue roomQueue, Integer ms) throws RequestException, DatabaseException;

    void repeat(RoomQueue roomQueue) throws RequestException, DatabaseException;

}
