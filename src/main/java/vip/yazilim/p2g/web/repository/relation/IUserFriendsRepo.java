package vip.yazilim.p2g.web.repository.relation;

import org.springframework.data.jpa.repository.JpaRepository;
import vip.yazilim.p2g.web.entity.relation.UserFriends;

import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IUserFriendsRepo extends JpaRepository<UserFriends, String> {

    Optional<UserFriends> findByUuid(String uuid);
    List<UserFriends> findByUserUuid(String userUuid);
    
}
