package vip.yazilim.p2g.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vip.yazilim.p2g.web.entity.FriendRequest;

import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IFriendRequestRepo extends JpaRepository<FriendRequest, Long> {

    List<FriendRequest> findBySenderUserId(String senderUserId);

    List<FriendRequest> findBySenderUserIdOrReceiverUserId(String senderUserId, String receiverUserId);

    List<FriendRequest> findByReceiverUserIdAndRequestStatusNot(String receiverUserId, String requestStatus);

    Optional<FriendRequest> findBySenderUserIdAndReceiverUserId(String senderUserId, String receiverUserId);

}
