package vip.yazilim.p2g.web.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.entity.relation.RoomUser;
import vip.yazilim.p2g.web.repository.IRoomRepo;
import vip.yazilim.p2g.web.service.IRoomService;
import vip.yazilim.p2g.web.service.relation.IRoomUserService;
import vip.yazilim.spring.utils.exception.DatabaseException;
import vip.yazilim.spring.utils.service.ACrudServiceImpl;

import java.util.Optional;
import java.util.stream.Stream;

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

    @Autowired
    private IRoomUserService roomUserService;

    @Override
    public Optional<Room> getRoomByUserUuid(String userUuid) throws DatabaseException {
        Optional<Room> room;
        Optional<RoomUser> roomUser;

        roomUser = roomUserService.getRoomUserByUserUuid(userUuid);
        String roomUuid = roomUser.get().getRoomUuid();

        try {
            room = getById(roomUuid);
        } catch (Exception exception) {
            String errorMessage = String.format("An error occurred while getting Room with userUuid[%s]", userUuid);
            throw new DatabaseException(errorMessage, exception);
        }

        if(!room.isPresent()){
            LOGGER.warn("User[{}] not in any Room!", userUuid);
            return Optional.empty();
        }

        return room;
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
