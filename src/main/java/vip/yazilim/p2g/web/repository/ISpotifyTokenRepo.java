package vip.yazilim.p2g.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vip.yazilim.p2g.web.entity.OAuthToken;

import java.util.Optional;
import java.util.UUID;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ISpotifyTokenRepo extends JpaRepository<OAuthToken, UUID> {

    Optional<OAuthToken> findSpotifyTokenByUserUuid(UUID userUuid);

}
