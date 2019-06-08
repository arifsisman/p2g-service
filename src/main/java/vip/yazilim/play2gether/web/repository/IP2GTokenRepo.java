package vip.yazilim.play2gether.web.repository;

import org.springframework.data.repository.CrudRepository;
import vip.yazilim.play2gether.web.entity.ListenQueue;
import vip.yazilim.play2gether.web.entity.P2GToken;

/**
 * @author Emre Sen - 21.05.2019
 * @contact maemresen07@gmail.com
 */
public interface IP2GTokenRepo extends CrudRepository<P2GToken, String> {

}
