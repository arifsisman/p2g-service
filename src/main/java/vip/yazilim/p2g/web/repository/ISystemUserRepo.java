package vip.yazilim.p2g.web.repository;

import vip.yazilim.p2g.web.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * @author Emre Sen - 21.05.2019
 * @contact maemresen07@gmail.com
 */
public interface ISystemUserRepo extends CrudRepository<User, String> {

    Optional<User> findByEmail(String email);
    Optional<User> findByUuid(String uuid);

}
