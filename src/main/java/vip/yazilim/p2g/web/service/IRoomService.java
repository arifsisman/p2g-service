package vip.yazilim.p2g.web.service;

import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.entity.relation.RoomUser;
import vip.yazilim.p2g.web.exception.RoomException;
import vip.yazilim.p2g.web.model.RoomModel;
import vip.yazilim.spring.utils.exception.DatabaseException;
import vip.yazilim.spring.utils.service.ICrudService;

import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IRoomService extends ICrudService<Room, String> {

    Optional<Room> getRoomByUserUuid(String userUuid) throws DatabaseException;
    Optional<RoomModel> getRoomModelByRoomUuid(String uuid) throws DatabaseException;

    Room createRoom(String name, String ownerUuid, String password, Integer maxUsers,
                    Boolean usersAllowedQueue, Boolean usersAllowedControl, String chatUuid) throws RoomException;

    RoomUser joinRoom(String roomUuid, String userUuid);
}