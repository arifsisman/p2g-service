package vip.yazilim.p2g.web.repository.relation;

import org.springframework.data.jpa.repository.JpaRepository;
import vip.yazilim.p2g.web.entity.relation.RoomInvite;

import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IRoomInvite extends JpaRepository<RoomInvite, String> {

    Optional<RoomInvite> findByUuid(String uuid);
    Iterable<RoomInvite> findByRoomId(String roomId);
    Iterable<RoomInvite> findByUserId(String userId);

}
