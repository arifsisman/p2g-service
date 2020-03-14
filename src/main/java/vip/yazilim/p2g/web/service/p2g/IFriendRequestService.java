package vip.yazilim.p2g.web.service.p2g;

import vip.yazilim.libs.springcore.exception.general.BusinessLogicException;
import vip.yazilim.libs.springcore.exception.general.InvalidArgumentException;
import vip.yazilim.libs.springcore.exception.general.database.DatabaseException;
import vip.yazilim.libs.springcore.exception.general.database.DatabaseReadException;
import vip.yazilim.libs.springcore.service.ICrudService;
import vip.yazilim.p2g.web.constant.enums.FriendRequestStatus;
import vip.yazilim.p2g.web.entity.FriendRequest;
import vip.yazilim.p2g.web.model.FriendModel;
import vip.yazilim.p2g.web.model.FriendRequestModel;

import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 2.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IFriendRequestService extends ICrudService<FriendRequest, Long> {

    List<FriendRequest> getFriendRequestsByReceiverId(String userId) throws DatabaseException;
    List<FriendModel> getFriendsByUserId(String userId) throws DatabaseException, InvalidArgumentException;
    Integer getFriendsCountByUserId(String userId) throws DatabaseReadException;
    List<FriendRequestModel> getFriendRequestModelByReceiverId(String userId) throws DatabaseException, InvalidArgumentException;

    Optional<FriendRequest> getFriendRequestBySenderIdAndReceiverId(String user1, String user2) throws DatabaseReadException;

    Optional<FriendRequest> getFriendRequestBySenderIdAndReceiverIdAndRequestStatus(String senderId, String receiverId, FriendRequestStatus requestStatus) throws DatabaseReadException;

    boolean createFriendRequest(String senderId, String receiverId) throws BusinessLogicException;

    boolean acceptFriendRequest(Long friendRequestId) throws DatabaseException, InvalidArgumentException;
    boolean ignoreFriendRequest(Long friendRequestId) throws DatabaseException, InvalidArgumentException;
    boolean rejectFriendRequest(Long friendRequestId) throws DatabaseException, InvalidArgumentException;
    boolean deleteFriend(String friendId) throws DatabaseException;
}
