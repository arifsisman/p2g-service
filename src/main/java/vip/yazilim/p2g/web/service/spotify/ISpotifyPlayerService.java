package vip.yazilim.p2g.web.service.spotify;

import vip.yazilim.p2g.web.entity.relation.RoomQueue;
import vip.yazilim.p2g.web.exception.PlayerException;
import vip.yazilim.p2g.web.exception.QueueException;
import vip.yazilim.p2g.web.exception.RequestException;
import vip.yazilim.spring.utils.exception.DatabaseException;
import vip.yazilim.spring.utils.exception.InvalidUpdateException;

import java.util.List;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ISpotifyPlayerService {

    List<RoomQueue> play(String roomQueueUuid) throws RequestException, PlayerException, DatabaseException, QueueException, InvalidUpdateException;

    List<RoomQueue> resume(String roomUuid) throws RequestException, DatabaseException, InvalidUpdateException, PlayerException;

    List<RoomQueue> pause(String roomUuid) throws RequestException, DatabaseException, InvalidUpdateException, PlayerException;

    List<RoomQueue> next(String roomUuid) throws RequestException, DatabaseException, PlayerException, QueueException, InvalidUpdateException;

    List<RoomQueue> previous(String roomUuid) throws RequestException, DatabaseException, PlayerException, QueueException, InvalidUpdateException;

    List<RoomQueue> seek(String roomUuid, Integer ms) throws RequestException, DatabaseException, PlayerException;

    List<RoomQueue> repeat(String roomUuid) throws RequestException, DatabaseException, PlayerException;

}
