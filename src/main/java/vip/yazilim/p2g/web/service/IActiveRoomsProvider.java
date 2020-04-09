package vip.yazilim.p2g.web.service;

import vip.yazilim.p2g.web.entity.Room;

import java.util.List;

/**
 * @author mustafaarifsisman - 09.04.2020
 * @contact mustafaarifsisman@gmail.com
 */
public interface IActiveRoomsProvider {
    List<Room> getActiveRooms();

    void activateRoom(Long roomId);

    void deactivateRoom(Room room);
}
