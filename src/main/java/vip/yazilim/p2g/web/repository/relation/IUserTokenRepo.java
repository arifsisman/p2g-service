package vip.yazilim.p2g.web.repository.relation;

import org.springframework.data.jpa.repository.JpaRepository;
import vip.yazilim.p2g.web.entity.relation.UserToken;

import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IUserTokenRepo extends JpaRepository<UserToken, String> {

    Optional<UserToken> findByUuid(String uuid);
    List<UserToken> findTokensByUserUuid(String userUuid);

}
