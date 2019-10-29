package vip.yazilim.p2g.web.repository.relation;

import org.springframework.data.repository.CrudRepository;
import vip.yazilim.p2g.web.entity.relation.UserSettings;

import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IUserSettings extends CrudRepository<UserSettings, String> {

    Optional<UserSettings> findByUuid(String uuid);
    Optional<UserSettings> findByUserUuid(String userUuid);

}
