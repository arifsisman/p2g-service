package vip.yazilim.p2g.web.service.p2g.relation;

import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.exception.InviteException;
import vip.yazilim.spring.utils.exception.DatabaseException;

import java.util.List;

/**
 * @author mustafaarifsisman - 26.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IRoomInviteService {

    List<User> getInvitedUserListByRoomUuid(String roomUuid) throws DatabaseException;
    void acceptInviteByUuid(String roomInviteUuid) throws DatabaseException, InviteException;
    void rejectInviteByUuid(String roomInviteUuid) throws DatabaseException, InviteException;

}
