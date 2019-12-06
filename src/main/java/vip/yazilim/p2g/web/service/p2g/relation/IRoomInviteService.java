package vip.yazilim.p2g.web.service.p2g.relation;

import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.entity.relation.RoomInvite;
import vip.yazilim.spring.core.exception.InvalidArgumentException;
import vip.yazilim.spring.core.exception.InvalidUpdateException;
import vip.yazilim.spring.core.exception.database.DatabaseException;
import vip.yazilim.spring.core.service.ICrudService;

import java.util.List;

/**
 * @author mustafaarifsisman - 26.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IRoomInviteService extends ICrudService<RoomInvite, String> {

    List<User> getInvitedUserListByRoomUuid(String roomUuid) throws DatabaseException;

    // Rest
    RoomInvite accept(RoomInvite roomInvite) throws DatabaseException, InvalidUpdateException, InvalidArgumentException;
    boolean reject(String roomInviteUuid) throws DatabaseException;
    boolean deleteRoomInvites(String roomUuid) throws DatabaseException;
}
