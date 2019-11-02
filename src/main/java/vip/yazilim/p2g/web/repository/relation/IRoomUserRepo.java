package vip.yazilim.p2g.web.repository.relation;

import org.springframework.data.jpa.repository.JpaRepository;
import vip.yazilim.p2g.web.entity.relation.RoomUser;

import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IRoomUserRepo extends JpaRepository<RoomUser, String> {

    Optional<RoomUser> findByRoomUuidAndUserUuid(String roomUuid, String userUuid);

    List<RoomUser> findByUserUuid(String userUuid);
}
