package vip.yazilim.p2g.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vip.yazilim.p2g.web.entity.User;

/**
 * @author mustafaarifsisman - 26.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IUserRepo extends JpaRepository<User, String> {

}
