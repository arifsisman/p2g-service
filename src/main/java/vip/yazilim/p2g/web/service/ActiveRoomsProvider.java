package vip.yazilim.p2g.web.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.service.p2g.IRoomService;

import java.util.LinkedList;
import java.util.List;

/**
 * @author mustafaarifsisman - 09.04.2020
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class ActiveRoomsProvider implements IActiveRoomsProvider {

    @Autowired
    private IRoomService roomService;

    private List<Room> activeRooms = new LinkedList<>();

    private Logger LOGGER = LoggerFactory.getLogger(ActiveRoomsProvider.class);

    @Override
    public List<Room> getActiveRooms() {
        return activeRooms;
    }

    @Override
    public void activateRoom(Room room) {
        if (!activeRooms.contains(room)) {
            activeRooms.add(room);
            LOGGER.debug("Room[{}] activated", room.getId());
        }
    }

    @Override
    public void deactivateRoom(Room room) {
        activeRooms.remove(room);
        LOGGER.debug("Room[{}] deactivated", room.getId());
    }

    @EventListener(ApplicationReadyEvent.class)
    public void activeCurrentRooms() {
        for (Room room : roomService.getAll()) {
            activateRoom(room);
        }
    }
}
