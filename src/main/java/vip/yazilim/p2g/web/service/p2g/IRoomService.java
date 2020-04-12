package vip.yazilim.p2g.web.service.p2g;

import vip.yazilim.libs.springcore.service.ICrudService;
import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.model.RoomModel;

import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IRoomService extends ICrudService<Room, Long> {
    Optional<Room> getRoomByUserId(String userId);

    List<RoomModel> getRoomModels();

    RoomModel getRoomModelByRoomId(Long roomId);

    RoomModel getRoomModelWithRoom(Room room);

    Room createRoom(String ownerId, String roomName, String roomPassword);

    RoomModel getRoomModelByUserId(String userId);

    List<Room> getActiveRooms();

    Optional<User> getRoomOwner(Long roomId);
}