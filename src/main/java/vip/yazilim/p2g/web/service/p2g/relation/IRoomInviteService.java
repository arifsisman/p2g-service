package vip.yazilim.p2g.web.service.p2g.relation;

import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.entity.relation.RoomInvite;
import vip.yazilim.p2g.web.exception.InviteException;
import vip.yazilim.spring.core.exception.InvalidArgumentException;
import vip.yazilim.spring.core.exception.database.DatabaseException;
import vip.yazilim.spring.core.service.ICrudService;

import java.util.List;

/**
 * @author mustafaarifsisman - 26.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IRoomInviteService extends ICrudService<RoomInvite, String> {

    List<User> getInvitedUserListByRoomUuid(String roomUuid) throws DatabaseException;
    void acceptInviteByUuid(String roomInviteUuid) throws DatabaseException, InviteException, InvalidArgumentException;
    void rejectInviteByUuid(String roomInviteUuid) throws DatabaseException, InviteException, InvalidArgumentException;

}
