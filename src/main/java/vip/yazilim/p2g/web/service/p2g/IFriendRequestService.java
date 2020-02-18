package vip.yazilim.p2g.web.service.p2g;

import vip.yazilim.p2g.web.entity.FriendRequest;
import vip.yazilim.p2g.web.model.FriendModel;
import vip.yazilim.p2g.web.model.FriendRequestModel;
import vip.yazilim.spring.core.exception.GeneralException;
import vip.yazilim.spring.core.exception.InvalidArgumentException;
import vip.yazilim.spring.core.exception.database.DatabaseException;
import vip.yazilim.spring.core.exception.database.DatabaseReadException;
import vip.yazilim.spring.core.service.ICrudService;

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
    boolean createFriendRequest(String senderId, String receiverId) throws GeneralException;
    boolean acceptFriendRequest(Long friendRequestId) throws DatabaseException, InvalidArgumentException;
    boolean ignoreFriendRequest(Long friendRequestId) throws DatabaseException, InvalidArgumentException;
    boolean rejectFriendRequest(Long friendRequestId) throws DatabaseException, InvalidArgumentException;

    boolean deleteFriend(String friendId) throws DatabaseException;
}
