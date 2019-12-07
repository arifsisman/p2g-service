package vip.yazilim.p2g.web.service.spotify;

import vip.yazilim.p2g.web.entity.relation.RoomQueue;
import vip.yazilim.p2g.web.exception.PlayerException;
import vip.yazilim.p2g.web.exception.QueueException;
import vip.yazilim.p2g.web.exception.RequestException;
import vip.yazilim.spring.core.exception.InvalidArgumentException;
import vip.yazilim.spring.core.exception.InvalidUpdateException;
import vip.yazilim.spring.core.exception.database.DatabaseException;

import java.util.List;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ISpotifyPlayerService {

    List<RoomQueue> playRoom(String roomUuid) throws RequestException, PlayerException, DatabaseException, QueueException, InvalidUpdateException, InvalidArgumentException;

    List<RoomQueue> playQueue(RoomQueue roomQueue) throws RequestException, PlayerException, DatabaseException, QueueException, InvalidUpdateException, InvalidArgumentException;

    List<RoomQueue> resume(String roomUuid) throws RequestException, DatabaseException, InvalidUpdateException, PlayerException, InvalidArgumentException;

    List<RoomQueue> pause(String roomUuid) throws RequestException, DatabaseException, InvalidUpdateException, PlayerException, InvalidArgumentException;

    List<RoomQueue> next(String roomUuid) throws RequestException, DatabaseException, PlayerException, QueueException, InvalidUpdateException, InvalidArgumentException;

    List<RoomQueue> previous(String roomUuid) throws RequestException, DatabaseException, PlayerException, QueueException, InvalidUpdateException, InvalidArgumentException;

    List<RoomQueue> seek(String roomUuid, Integer ms) throws RequestException, DatabaseException, PlayerException, InvalidArgumentException;

    List<RoomQueue> repeat(String roomUuid) throws RequestException, DatabaseException, PlayerException, InvalidArgumentException;

}
