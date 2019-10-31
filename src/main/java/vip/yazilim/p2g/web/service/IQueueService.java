package vip.yazilim.p2g.web.service;

import vip.yazilim.p2g.web.entity.Queue;
import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.p2g.web.exception.DatabaseException;

import java.util.List;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IQueueService extends ICrudService<Queue, String> {

    List<Song> getSongsByRoomUuid(String roomUuid) throws DatabaseException;

}
