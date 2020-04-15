package vip.yazilim.p2g.web.service.p2g;

import vip.yazilim.libs.springcore.service.ICrudService;
import vip.yazilim.p2g.web.entity.FriendRequest;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.enums.FriendRequestStatus;
import vip.yazilim.p2g.web.model.FriendRequestModel;
import vip.yazilim.p2g.web.model.UserFriendModel;
import vip.yazilim.p2g.web.model.UserModel;

import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 2.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IFriendRequestService extends ICrudService<FriendRequest, Long> {

    List<FriendRequest> getFriendRequestsByReceiverId(String userId);

    List<UserModel> getFriendsModelByUserId(String userId);

    List<User> getFriendsByUserId(String userId);

    Integer getFriendsCountByUserId(String userId);

    List<FriendRequestModel> getFriendRequestModelByReceiverId(String userId);

    Optional<FriendRequest> getFriendRequestBySenderIdAndReceiverId(String user1, String user2);

    Optional<FriendRequest> getFriendRequestBySenderIdAndReceiverIdAndRequestStatus(String senderId, String receiverId, FriendRequestStatus requestStatus);

    boolean createFriendRequest(String senderId, String receiverId);

    boolean acceptFriendRequest(Long friendRequestId);

    boolean ignoreFriendRequest(Long friendRequestId);

    boolean rejectFriendRequest(Long friendRequestId);

    boolean deleteFriend(String friendId);

    UserFriendModel getUserFriendModel(String userId);
}
