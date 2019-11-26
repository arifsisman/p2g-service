package vip.yazilim.p2g.web.repository.relation;

import org.springframework.data.jpa.repository.JpaRepository;
import vip.yazilim.p2g.web.entity.relation.RoomInvite;

import java.util.List;

/**
 * @author mustafaarifsisman - 26.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IRoomInviteRepo  extends JpaRepository<RoomInvite, String> {

    List<RoomInvite> findRoomInvitesByRoomUuid(String roomUuid);

}
