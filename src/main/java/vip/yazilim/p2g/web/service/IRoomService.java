package vip.yazilim.p2g.web.service;

import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.spring.utils.exception.DatabaseException;
import vip.yazilim.spring.utils.service.ICrudService;

import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IRoomService extends ICrudService<Room, String> {

    Optional<Room> getRoomByOwnerUuid(String ownerUuid) throws DatabaseException;
    boolean replyInviteByUuid(String roomUserUuid, boolean isAccepted) throws DatabaseException;

}