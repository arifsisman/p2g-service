package vip.yazilim.p2g.web.service.p2g.relation;

import vip.yazilim.p2g.web.entity.relation.RoomUser;
import vip.yazilim.p2g.web.exception.RoomException;
import vip.yazilim.spring.core.exception.InvalidArgumentException;
import vip.yazilim.spring.core.exception.database.DatabaseException;
import vip.yazilim.spring.core.service.ICrudService;

import java.util.List;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IRoomUserService extends ICrudService<RoomUser, String> {

    List<RoomUser> getRoomUsersByRoomUuid(String roomUuid) throws DatabaseException;
    RoomUser getRoomUser(String userUuid) throws DatabaseException;
    RoomUser getRoomUser(String roomUuid, String userUuid) throws DatabaseException;

    RoomUser joinRoom(String roomUuid, String userUuid, String password) throws RoomException, DatabaseException, InvalidArgumentException;

    // Rest
    boolean deleteRoomUsers(String roomUuid) throws DatabaseException;
}
