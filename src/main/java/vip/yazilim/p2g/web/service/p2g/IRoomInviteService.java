package vip.yazilim.p2g.web.service.p2g;

import vip.yazilim.libs.springcore.service.ICrudService;
import vip.yazilim.p2g.web.entity.RoomInvite;
import vip.yazilim.p2g.web.entity.RoomUser;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.model.RoomInviteModel;

import java.util.List;

/**
 * @author mustafaarifsisman - 26.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IRoomInviteService extends ICrudService<RoomInvite, Long> {

    List<User> getInvitedUserListByRoomId(Long roomId);

    List<RoomInvite> getRoomInvitesByUserId(String userId);

    List<RoomInviteModel> getRoomInviteModelListByUserId(String userId);

    RoomInvite invite(Long roomId, String userId);

    RoomUser accept(RoomInvite roomInvite);

    boolean reject(Long roomInviteId);

    boolean deleteRoomInvites(Long roomId);

    boolean existsById(Long roomInviteId);
}
