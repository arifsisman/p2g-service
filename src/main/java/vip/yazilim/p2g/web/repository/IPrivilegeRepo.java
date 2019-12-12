package vip.yazilim.p2g.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vip.yazilim.p2g.web.entity.Privilege;

import java.util.List;

/**
 * @author mustafaarifsisman - 12.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IPrivilegeRepo extends JpaRepository<Privilege, String> {

    List<Privilege> findByRoleName(String roleName);

}
