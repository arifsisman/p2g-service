package vip.yazilim.p2g.web.service.p2g.relation;

import vip.yazilim.p2g.web.entity.relation.UserDevice;
import vip.yazilim.spring.core.exception.general.InvalidArgumentException;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;
import vip.yazilim.spring.core.service.ICrudService;

import java.util.List;

/**
 * @author mustafaarifsisman - 30.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IUserDeviceService extends ICrudService<UserDevice, String> {

    List<UserDevice> getUserDevicesByUserUuid(String userUuid) throws DatabaseException;

    List<UserDevice> getUserDevicesByRoomUuid(String roomUuid) throws DatabaseException, InvalidArgumentException;
}
