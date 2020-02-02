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

    List<FriendRequest> findByUserId(String userId);

    List<FriendRequest> findByUserIdOrFriendIdAndRequestStatus(String userId, String friendId, String requestStatus);

    List<FriendRequest> findByFriendIdAndRequestStatusNot(String friendId, String requestStatus);

    Optional<FriendRequest> findByUserIdAndFriendId(String userId, String friendId);

}
