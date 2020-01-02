package vip.yazilim.p2g.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vip.yazilim.p2g.web.entity.FriendRequest;

import java.util.List;
import java.util.UUID;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IFriendRequestRepo extends JpaRepository<FriendRequest, Long> {

    List<FriendRequest> findByUserUuid(UUID userUuid);
    List<FriendRequest> findByFriendUuid(UUID friendUuid);
    FriendRequest findByUserUuidAndFriendUuid(UUID userUuid, UUID friendUuid);

}
