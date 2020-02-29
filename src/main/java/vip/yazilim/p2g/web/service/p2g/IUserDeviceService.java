package vip.yazilim.p2g.web.service.p2g;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import vip.yazilim.p2g.web.entity.UserDevice;
import vip.yazilim.spring.core.exception.GeneralException;
import vip.yazilim.spring.core.exception.database.DatabaseReadException;
import vip.yazilim.spring.core.service.ICrudService;

import java.io.IOException;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 30.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IUserDeviceService extends ICrudService<UserDevice, String> {

    Optional<UserDevice> getUsersActiveDevice(String userId) throws DatabaseReadException;

    UserDevice saveUsersActiveDevice(String userId, UserDevice userDevice) throws GeneralException, IOException, SpotifyWebApiException;

//    List<UserDevice> getUserDevicesByUserId(String userId) throws DatabaseException;
//    List<UserDevice> getUserDevicesByRoomId(Long roomId) throws DatabaseException, InvalidArgumentException;

}
