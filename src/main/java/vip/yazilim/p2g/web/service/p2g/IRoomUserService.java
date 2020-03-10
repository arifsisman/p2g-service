package vip.yazilim.p2g.web.service.p2g;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import vip.yazilim.p2g.web.constant.enums.Privilege;
import vip.yazilim.p2g.web.constant.enums.Role;
import vip.yazilim.p2g.web.entity.RoomInvite;
import vip.yazilim.p2g.web.entity.RoomUser;
import vip.yazilim.p2g.web.model.RoomUserModel;
import vip.yazilim.spring.core.exception.GeneralException;
import vip.yazilim.spring.core.exception.InvalidArgumentException;
import vip.yazilim.spring.core.exception.database.DatabaseException;
import vip.yazilim.spring.core.exception.database.DatabaseReadException;
import vip.yazilim.spring.core.service.ICrudService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IRoomUserService extends ICrudService<RoomUser, Long> {

    List<RoomUser> getRoomUsersByRoomId(Long roomId) throws DatabaseException;
    Optional<RoomUser> getRoomUser(String userId) throws DatabaseException;

    RoomUserModel getRoomUserModelMe(String userId) throws DatabaseException, InvalidArgumentException;

    Optional<RoomUser> getRoomUser(Long roomId, String userId) throws DatabaseException;
    Optional<RoomUser> getRoomOwner(Long roomId) throws DatabaseException;
    RoomUser joinRoom(Long roomId, String userId, String password, Role role) throws GeneralException, IOException, SpotifyWebApiException;
    RoomUser joinRoomOwner(Long roomId, String userId) throws GeneralException;

    boolean leaveRoom() throws DatabaseException, InvalidArgumentException;

    List<RoomUserModel> getRoomUserModelsByRoomId(Long roomId) throws DatabaseException, InvalidArgumentException;

    RoomUser acceptRoomInvite(RoomInvite roomInvite) throws GeneralException;
    Role getRoleByRoomIdAndUserId(Long roomId, String userId) throws DatabaseException;
    boolean deleteRoomUsers(Long roomId) throws DatabaseException;

    RoomUser changeRoomUserRole(Long roomUserId, boolean promoteDemoteFlag) throws DatabaseException, InvalidArgumentException;

    boolean hasRoomPrivilege(String userId, Privilege privilege) throws DatabaseException;
    boolean hasRoomRole(String userId, Role role) throws DatabaseException;
    int getRoomUserCountByRoomId(Long roomId) throws DatabaseReadException;
}
