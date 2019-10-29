package vip.yazilim.p2g.web.service;

import vip.yazilim.p2g.web.entity.Queue;
import vip.yazilim.p2g.web.entity.Song;

import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IQueueService extends ICrudService<Queue, String> {

    Optional<List<Song>> getSongsByRoomUuid(String roomUuid);

}
