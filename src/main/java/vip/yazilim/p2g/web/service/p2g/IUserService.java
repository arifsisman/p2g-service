package vip.yazilim.p2g.web.service.p2g;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import vip.yazilim.p2g.web.constant.Privilege;
import vip.yazilim.p2g.web.constant.Role;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.model.UserModel;
import vip.yazilim.spring.core.exception.general.InvalidArgumentException;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;
import vip.yazilim.spring.core.service.ICrudService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IUserService extends ICrudService<User, String> {

    Optional<User> getUserByUsername(String username);
    Optional<User> getUserByEmail(String email) throws DatabaseException;
    Optional<User> getUserByUuid(String uuid);
    UserModel getUserModelByUserUuid(String userUuid) throws DatabaseException, InvalidArgumentException;
    List<User> getUsersByRoomUuid(String roomUuid) throws DatabaseException, InvalidArgumentException;

    User createUser(String email, String username, String password) throws DatabaseException, InvalidArgumentException;

    User setSpotifyInfo(com.wrapper.spotify.model_objects.specification.User spotifyUser, User user) throws DatabaseException, InvalidArgumentException, IOException, SpotifyWebApiException;

    // Rest
    boolean hasSystemRole(String userUuid, Role role) throws DatabaseException, InvalidArgumentException;

    boolean hasSystemPrivilege(String userUuid, Privilege privilege) throws DatabaseException, InvalidArgumentException;
}
