package vip.yazilim.p2g.web.service.p2g;

import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.model.RoomModel;
import vip.yazilim.spring.core.exception.general.InvalidArgumentException;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;
import vip.yazilim.spring.core.service.ICrudService;

import java.util.Optional;
import java.util.UUID;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IRoomService extends ICrudService<Room, UUID> {

    Optional<Room> getRoomByUserId(String userId) throws DatabaseException;

    //Rest
    RoomModel getRoomModelByRoomUuid(UUID roomUuid) throws DatabaseException, InvalidArgumentException;
    Room createRoom(String ownerUuid, String roomName, String roomPassword) throws DatabaseException, InvalidArgumentException;

}