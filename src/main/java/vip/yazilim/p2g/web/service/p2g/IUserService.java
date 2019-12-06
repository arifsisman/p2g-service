package vip.yazilim.p2g.web.service.p2g;

import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.exception.*;
import vip.yazilim.p2g.web.model.UserModel;
import vip.yazilim.spring.core.exception.InvalidArgumentException;
import vip.yazilim.spring.core.exception.database.DatabaseException;
import vip.yazilim.spring.core.service.ICrudService;

import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IUserService extends ICrudService<User, String> {

    Optional<User> getUserByEmail(String email) throws DatabaseException;
    Optional<User> getUserByUuid(String uuid);
    Optional<UserModel> getUserModelByUserUuid(String userUuid) throws DatabaseException, RoleException, RoomException, InvalidArgumentException;
    List<User> getUsersByRoomUuid(String roomUuid) throws DatabaseException, InvalidArgumentException;

    User setSpotifyInfo(com.wrapper.spotify.model_objects.specification.User spotifyUser, User user) throws DatabaseException, TokenException, RequestException, AccountException;
}
