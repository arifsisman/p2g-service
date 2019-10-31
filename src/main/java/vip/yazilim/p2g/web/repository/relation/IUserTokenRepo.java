package vip.yazilim.p2g.web.repository.relation;

import org.springframework.data.jpa.repository.JpaRepository;
import vip.yazilim.p2g.web.entity.relation.UserSettings;
import vip.yazilim.p2g.web.entity.relation.UserToken;

import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IUserTokenRepo extends JpaRepository<UserToken, String> {

    Optional<UserSettings> findByUuid(String uuid);

}
