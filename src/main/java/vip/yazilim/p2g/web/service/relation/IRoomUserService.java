package vip.yazilim.p2g.web.service.relation;

import vip.yazilim.p2g.web.entity.relation.RoomUser;
import vip.yazilim.p2g.web.exception.InviteException;
import vip.yazilim.spring.utils.exception.DatabaseException;
import vip.yazilim.spring.utils.service.ICrudService;

import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IRoomUserService extends ICrudService<RoomUser, String> {

    List<RoomUser> getRoomUsersByRoomUuid(String roomUuid) throws DatabaseException;
    Optional<RoomUser> getRoomUser(String roomUuid, String userUuid) throws DatabaseException;

    void acceptInviteByUuid(String roomUserUuid) throws DatabaseException, InviteException;
    void rejectInviteByUuid(String roomUserUuid) throws DatabaseException, InviteException;

}
