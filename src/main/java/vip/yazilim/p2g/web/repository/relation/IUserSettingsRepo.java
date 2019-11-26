package vip.yazilim.p2g.web.repository.relation;

import org.springframework.data.jpa.repository.JpaRepository;
import vip.yazilim.p2g.web.entity.relation.UserSettings;

import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IUserSettingsRepo extends JpaRepository<UserSettings, String> {

    Optional<UserSettings> findByUuid(String uuid);
    UserSettings findByUserUuid(String userUuid);

}
