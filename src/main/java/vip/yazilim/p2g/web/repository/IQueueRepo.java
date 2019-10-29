package vip.yazilim.p2g.web.repository;

import org.springframework.data.repository.CrudRepository;
import vip.yazilim.p2g.web.entity.Queue;

import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IQueueRepo extends CrudRepository<Queue, String> {

    Optional<Queue> findByUuid(String uuid);
    Optional<Queue> findByRoomId(String roomId);

}
