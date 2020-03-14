package vip.yazilim.p2g.web.service.p2g;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import vip.yazilim.libs.springcore.exception.general.BusinessLogicException;
import vip.yazilim.libs.springcore.exception.general.database.DatabaseReadException;
import vip.yazilim.libs.springcore.service.ICrudService;
import vip.yazilim.p2g.web.entity.UserDevice;

import java.io.IOException;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 30.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IUserDeviceService extends ICrudService<UserDevice, String> {

    Optional<UserDevice> getUsersActiveDevice(String userId) throws DatabaseReadException;

    UserDevice saveUsersActiveDevice(String userId, UserDevice userDevice) throws BusinessLogicException, IOException, SpotifyWebApiException;

}
