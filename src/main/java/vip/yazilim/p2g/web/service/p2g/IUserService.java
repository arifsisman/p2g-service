package vip.yazilim.p2g.web.service.p2g;

import vip.yazilim.p2g.web.constant.Privilege;
import vip.yazilim.p2g.web.constant.Role;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.exception.AccountException;
import vip.yazilim.p2g.web.exception.RequestException;
import vip.yazilim.p2g.web.exception.RoomException;
import vip.yazilim.p2g.web.exception.TokenException;
import vip.yazilim.p2g.web.model.UserModel;
import vip.yazilim.spring.core.exception.general.InvalidArgumentException;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;
import vip.yazilim.spring.core.service.ICrudService;

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
    Optional<UserModel> getUserModelByUserUuid(String userUuid) throws DatabaseException, RoomException, InvalidArgumentException;
    List<User> getUsersByRoomUuid(String roomUuid) throws DatabaseException, InvalidArgumentException;

    User createUser(String email, String username, String password) throws DatabaseException, InvalidArgumentException;

    User setSpotifyInfo(com.wrapper.spotify.model_objects.specification.User spotifyUser, User user) throws DatabaseException, TokenException, RequestException, AccountException, InvalidArgumentException;

    // Rest
    boolean hasSystemRole(String userUuid, Role role) throws DatabaseException, InvalidArgumentException;

    boolean hasSystemPrivilege(String userUuid, Privilege privilege) throws DatabaseException, InvalidArgumentException;
}
