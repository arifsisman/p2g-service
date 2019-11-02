package vip.yazilim.p2g.web.repository.relation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import vip.yazilim.p2g.web.entity.relation.RoomUser;
import vip.yazilim.p2g.web.entity.relation.UserFriendRequests;

import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IUserFriendRequestsRepo extends JpaRepository<UserFriendRequests, String> {

    Optional<UserFriendRequests> findByUuid(String uuid);
    List<UserFriendRequests> findByUserUuid(String userUuid);

}
