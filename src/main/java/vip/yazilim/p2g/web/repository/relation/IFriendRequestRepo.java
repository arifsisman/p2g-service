package vip.yazilim.p2g.web.repository.relation;

import org.springframework.data.jpa.repository.JpaRepository;
import vip.yazilim.p2g.web.entity.relation.FriendRequest;

import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IFriendRequestRepo extends JpaRepository<FriendRequest, String> {

    Optional<FriendRequest> findByUuid(String uuid);
    List<FriendRequest> findByUserUuid(String userUuid);
    List<FriendRequest> findByFriendUuid(String friendUuid);

    FriendRequest findByUserUuidAndFriendUuid(String userUuid, String friendUuid);

}
