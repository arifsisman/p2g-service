package vip.yazilim.p2g.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.exception.RoomException;
import vip.yazilim.p2g.web.model.RoomModel;
import vip.yazilim.p2g.web.service.p2g.IRoomService;
import vip.yazilim.spring.core.exception.InvalidArgumentException;
import vip.yazilim.spring.core.exception.database.DatabaseException;
import vip.yazilim.spring.core.rest.ARestCrud;
import vip.yazilim.spring.core.service.ICrudService;

import java.util.Optional;

import static vip.yazilim.p2g.web.constant.Constants.API_P2G;

/**
 * @author mustafaarifsisman - 5.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@RestController
@RequestMapping(API_P2G + "/room")
public class RoomRest extends ARestCrud<Room, String> {

    @Autowired
    private IRoomService roomService;

    @Override
    protected ICrudService<Room, String> getService() {
        return roomService;
    }

    @GetMapping("/model/{roomUuid}")
    public Optional<RoomModel> getRoomModel(@PathVariable String roomUuid) throws DatabaseException, RoomException, InvalidArgumentException {
        return roomService.getRoomModelByRoomUuid(roomUuid);
    }
}
