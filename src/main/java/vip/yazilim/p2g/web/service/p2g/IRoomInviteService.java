package vip.yazilim.p2g.web.service.p2g;

import vip.yazilim.p2g.web.entity.RoomInvite;
import vip.yazilim.p2g.web.entity.RoomUser;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.spring.core.exception.general.InvalidArgumentException;
import vip.yazilim.spring.core.exception.general.InvalidUpdateException;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;
import vip.yazilim.spring.core.service.ICrudService;

import java.util.List;

/**
 * @author mustafaarifsisman - 26.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IRoomInviteService extends ICrudService<RoomInvite, String> {

    List<User> getInvitedUserListByRoomUuid(String roomUuid) throws DatabaseException;

    // Rest
    RoomInvite invite(String roomUuid, String userUuid) throws DatabaseException, InvalidArgumentException;
    RoomUser accept(RoomInvite roomInvite) throws DatabaseException, InvalidUpdateException, InvalidArgumentException;
    boolean reject(String roomInviteUuid) throws DatabaseException, InvalidArgumentException;
    boolean deleteRoomInvites(String roomUuid) throws DatabaseException;
    boolean existsById(String roomInviteUuid) throws DatabaseException;
}
