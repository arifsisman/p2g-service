package vip.yazilim.p2g.web.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.exception.DatabaseException;
import vip.yazilim.p2g.web.repository.IRoomRepo;
import vip.yazilim.p2g.web.service.IRoomService;

import java.util.Optional;

/**
 * @author mustafaarifsisman - 31.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class RoomServiceImpl extends ACrudServiceImpl<Room, String> implements IRoomService {

    // static fields
    private Logger LOGGER = LoggerFactory.getLogger(RoomServiceImpl.class);

    // injected dependencies
    @Autowired
    private IRoomRepo roomRepo;

    @Override
    public Optional<Room> getRoomByUuid(String roomUuid) throws DatabaseException {
        Optional<Room> roomOptional;

        try {
            roomOptional = roomRepo.findByUuid(roomUuid);
        } catch (Exception exception) {
            String errorMessage = String.format("An error occurred while getting Room with uuid[%s]", roomUuid);
            throw new DatabaseException(errorMessage, exception);
        }

        return roomOptional;
    }

    @Override
    public Optional<Room> getRoomByOwnerUuid(String ownerUuid) throws DatabaseException {
        Optional<Room> roomOptional;

        try {
            roomOptional = roomRepo.findByOwnerUuid(ownerUuid);
        } catch (Exception exception) {
            String errorMessage = String.format("An error occurred while getting Room with ownerUuid[%s]", ownerUuid);
            throw new DatabaseException(errorMessage, exception);
        }

        return roomOptional;
    }

    @Override
    protected JpaRepository<Room, String> getRepository() {
        return roomRepo;
    }

    @Override
    protected String getId(Room entity) {
        return entity.getUuid();
    }

}
