package vip.yazilim.p2g.web.repository.relation;

import org.springframework.data.repository.CrudRepository;
import vip.yazilim.p2g.web.entity.relation.RoomUser;
import vip.yazilim.p2g.web.entity.relation.UserFriendRequests;

import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IUserFriendRequests extends CrudRepository<UserFriendRequests, String> {

    Optional<RoomUser> findByUuid(String uuid);
    Iterable<RoomUser> findByUserUuid(String userUuid);

}
