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

    List<FriendRequest> findBySenderId(String senderUserId);

    List<FriendRequest> findBySenderIdOrReceiverId(String senderId, String receiverId);

    List<FriendRequest> findByReceiverIdAndRequestStatusNot(String receiverId, String requestStatus);

    Optional<FriendRequest> findBySenderIdAndReceiverId(String senderId, String receiverId);

}
