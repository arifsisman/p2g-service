package vip.yazilim.play2gether.web.repository.old;

import vip.yazilim.play2gether.web.entity.old.Manager;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * @author Emre Sen - 21.05.2019
 * @contact maemresen07@gmail.com
 */
public interface IManagerRepo extends CrudRepository<Manager, String> {

    Optional<Manager> findBySystemUser_uuid(String uuid);

}
