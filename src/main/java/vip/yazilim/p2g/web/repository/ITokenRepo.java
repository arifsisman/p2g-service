package vip.yazilim.p2g.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vip.yazilim.p2g.web.entity.SpotifyToken;

import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ITokenRepo extends JpaRepository<SpotifyToken, String> {

    Optional<SpotifyToken> findSpotifyTokenByUserUuid(String userUuid);
}
