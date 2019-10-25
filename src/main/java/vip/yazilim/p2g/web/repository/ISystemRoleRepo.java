package vip.yazilim.p2g.web.repository;

import vip.yazilim.p2g.web.entity.Role;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Emre Sen - 21.05.2019
 * @contact maemresen07@gmail.com
 */
public interface ISystemRoleRepo extends CrudRepository<Role, String> {
}
