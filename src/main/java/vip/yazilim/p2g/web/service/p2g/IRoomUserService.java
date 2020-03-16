package vip.yazilim.p2g.web.service.p2g;

import vip.yazilim.libs.springcore.service.ICrudService;
import vip.yazilim.p2g.web.constant.enums.Privilege;
import vip.yazilim.p2g.web.constant.enums.Role;
import vip.yazilim.p2g.web.entity.RoomInvite;
import vip.yazilim.p2g.web.entity.RoomUser;
import vip.yazilim.p2g.web.model.RoomUserModel;

import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IRoomUserService extends ICrudService<RoomUser, Long> {

    List<RoomUser> getRoomUsersByRoomId(Long roomId);

    Optional<RoomUser> getRoomUser(String userId);

    RoomUserModel getRoomUserModelMe(String userId);

    Optional<RoomUser> getRoomUser(Long roomId, String userId);

    Optional<RoomUser> getRoomOwner(Long roomId);

    RoomUser joinRoom(Long roomId, String userId, String password, Role role);

    RoomUser joinRoomOwner(Long roomId, String userId);

    boolean leaveRoom();

    List<RoomUserModel> getRoomUserModelsByRoomId(Long roomId);

    RoomUser acceptRoomInvite(RoomInvite roomInvite);

    Role getRoleByRoomIdAndUserId(Long roomId, String userId);

    boolean deleteRoomUsers(Long roomId);

    RoomUser changeRoomUserRole(Long roomUserId, boolean promoteDemoteFlag);

    boolean hasRoomPrivilege(String userId, Privilege privilege);

    boolean hasRoomRole(String userId, Role role);

    int getRoomUserCountByRoomId(Long roomId);
}
