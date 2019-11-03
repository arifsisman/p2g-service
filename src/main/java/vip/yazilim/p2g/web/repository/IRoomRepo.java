package vip.yazilim.p2g.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vip.yazilim.p2g.web.entity.Room;

import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IRoomRepo extends JpaRepository<Room, String> {

    Optional<Room> findByUuid(String uuid);
    Optional<Room> findByUserUuid(String userUuid);

}
