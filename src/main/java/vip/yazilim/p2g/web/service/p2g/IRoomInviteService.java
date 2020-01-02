package vip.yazilim.p2g.web.service.p2g;

import vip.yazilim.p2g.web.entity.RoomInvite;
import vip.yazilim.p2g.web.entity.RoomUser;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.spring.core.exception.general.InvalidArgumentException;
import vip.yazilim.spring.core.exception.general.InvalidUpdateException;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;
import vip.yazilim.spring.core.service.ICrudService;

import java.util.List;
import java.util.UUID;

/**
 * @author mustafaarifsisman - 26.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IRoomInviteService extends ICrudService<RoomInvite, Long> {

    List<User> getInvitedUserListByRoomId(Long roomId) throws DatabaseException, InvalidArgumentException;

    // Rest
    RoomInvite invite(Long roomId, UUID userUuid) throws DatabaseException, InvalidArgumentException;
    RoomUser accept(RoomInvite roomInvite) throws DatabaseException, InvalidUpdateException, InvalidArgumentException;
    boolean reject(Long roomInviteId) throws DatabaseException, InvalidArgumentException;
    boolean deleteRoomInvites(Long roomId) throws DatabaseException;
    boolean existsById(Long roomInviteId) throws DatabaseException;
}
