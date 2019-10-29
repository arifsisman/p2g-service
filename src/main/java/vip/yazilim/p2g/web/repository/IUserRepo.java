package vip.yazilim.p2g.web.repository;

import org.springframework.data.repository.CrudRepository;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.entity.relation.RoomUser;

import java.util.Optional;

/**
 * @author Emre Sen - 21.05.2019
 * @contact maemresen07@gmail.com
 */
public interface IUserRepo extends CrudRepository<User, String> {

    Optional<User> findByEmail(String email);
    Optional<User> findByUuid(String uuid);

}
