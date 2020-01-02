package vip.yazilim.p2g.web.service.p2g;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import vip.yazilim.p2g.web.constant.Privilege;
import vip.yazilim.p2g.web.constant.Role;
import vip.yazilim.p2g.web.entity.RoomInvite;
import vip.yazilim.p2g.web.entity.RoomUser;
import vip.yazilim.spring.core.exception.general.InvalidArgumentException;
import vip.yazilim.spring.core.exception.general.InvalidUpdateException;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;
import vip.yazilim.spring.core.service.ICrudService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IRoomUserService extends ICrudService<RoomUser, Long> {

    List<RoomUser> getRoomUsersByRoomId(Long roomId) throws DatabaseException;
    Optional<RoomUser> getRoomUser(UUID userUuid) throws DatabaseException;
    Optional<RoomUser> getRoomUser(Long roomId, UUID userUuid) throws DatabaseException;

    RoomUser joinRoom(Long roomId, String password, Role role) throws DatabaseException, InvalidArgumentException, IOException, SpotifyWebApiException;

    RoomUser joinRoom(Long roomId, UUID userUuid, String password, Role role) throws DatabaseException, InvalidArgumentException, IOException, SpotifyWebApiException;
    RoomUser joinRoomOwner(Long roomId, UUID userUuid) throws DatabaseException, InvalidArgumentException;

    boolean leaveRoom() throws DatabaseException, InvalidArgumentException;

    RoomUser acceptRoomInvite(RoomInvite roomInvite) throws DatabaseException, InvalidArgumentException;

    Role getRoleByRoomIdAndUserUuid(Long roomId, UUID userUuid) throws DatabaseException;

    // Rest
    boolean deleteRoomUsers(Long roomId) throws DatabaseException;

    List<Privilege> promoteUserRole(Long roomUserId) throws DatabaseException, InvalidUpdateException, InvalidArgumentException;
    List<Privilege> demoteUserRole(Long roomUserId) throws DatabaseException, InvalidUpdateException, InvalidArgumentException;

    boolean hasRoomPrivilege(UUID userUuid, Privilege privilege) throws DatabaseException;

    boolean hasRoomRole(UUID userUuid, Role role) throws DatabaseException;
}
