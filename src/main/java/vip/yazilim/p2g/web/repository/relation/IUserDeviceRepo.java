package vip.yazilim.p2g.web.repository.relation;

import org.springframework.data.jpa.repository.JpaRepository;
import vip.yazilim.p2g.web.entity.relation.UserDevice;

import java.util.List;

/**
 * @author mustafaarifsisman - 30.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IUserDeviceRepo extends JpaRepository<UserDevice, String> {

    List<UserDevice> findUserDevicesByUserUuid(String userUuid);

}
