package vip.yazilim.p2g.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vip.yazilim.p2g.web.entity.RoomUser;

import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IRoomUserRepo extends JpaRepository<RoomUser, Long> {

    List<RoomUser> findRoomUserByRoomIdOrderById(Long roomId);
    Optional<RoomUser> findRoomUserByRoomIdAndUserId(Long roomId, String userId);
    Optional<RoomUser> findRoomUserByUserId(String userId);

}
