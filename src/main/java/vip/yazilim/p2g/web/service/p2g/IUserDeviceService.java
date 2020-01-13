package vip.yazilim.p2g.web.service.p2g;

import vip.yazilim.p2g.web.entity.UserDevice;
import vip.yazilim.spring.core.exception.general.InvalidArgumentException;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;
import vip.yazilim.spring.core.service.ICrudService;

import java.util.List;
import java.util.UUID;

/**
 * @author mustafaarifsisman - 30.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IUserDeviceService extends ICrudService<UserDevice, String> {

    List<UserDevice> getUserDevicesByUserUuid(UUID userUuid) throws DatabaseException;

    List<UserDevice> getUserDevicesByroomUuid(UUID roomUuid) throws DatabaseException, InvalidArgumentException;

}
