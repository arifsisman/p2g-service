package vip.yazilim.p2g.web.service;

import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IRoomService extends CrudService<Room, String> {

    Optional<Room> getRoomByUuid(String roomUuid);
    Optional<Room> getRoomByOwnerUuid(String ownerUuid);
    Optional<List<User>> getUsersByUuid(String roomUuid);
}
