package vip.yazilim.p2g.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vip.yazilim.p2g.web.entity.RoomInvite;

import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 26.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IRoomInviteRepo  extends JpaRepository<RoomInvite, Long> {

    List<RoomInvite> findByRoomId(Long roomId);
    Optional<RoomInvite> findByRoomIdAndUserId(Long roomId, String userId);
    List<RoomInvite> findByUserIdAndAcceptedFlag(String userId, boolean acceptedFlag);

}
