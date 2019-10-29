package vip.yazilim.p2g.web.repository.relation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import vip.yazilim.p2g.web.entity.relation.RoomUser;

import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IRoomUser extends JpaRepository<RoomUser, String> {

    Optional<RoomUser> findByUuid(String uuid);
    Iterable<RoomUser> findByRoomUuid(String roomUuid);
    Optional<RoomUser> findByUserUuid(String userUuid);

}
