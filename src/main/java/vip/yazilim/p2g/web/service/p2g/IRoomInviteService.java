package vip.yazilim.p2g.web.service.p2g;

import vip.yazilim.libs.springcore.exception.general.BusinessLogicException;
import vip.yazilim.libs.springcore.exception.general.InvalidArgumentException;
import vip.yazilim.libs.springcore.exception.general.database.DatabaseException;
import vip.yazilim.libs.springcore.exception.general.database.DatabaseReadException;
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

    List<User> getInvitedUserListByRoomId(Long roomId) throws DatabaseException, InvalidArgumentException;

    List<RoomInvite> getRoomInvitesByUserId(String userId) throws DatabaseReadException;

    List<RoomInviteModel> getRoomInviteModelListByUserId(String userId) throws DatabaseException, InvalidArgumentException;

    RoomInvite invite(Long roomId, String userId) throws BusinessLogicException;

    RoomUser accept(RoomInvite roomInvite) throws BusinessLogicException;

    boolean reject(Long roomInviteId) throws DatabaseException;

    boolean deleteRoomInvites(Long roomId) throws DatabaseException;

    boolean existsById(Long roomInviteId) throws DatabaseException;
}
