package vip.yazilim.p2g.web.service.p2g;

import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.exception.RequestException;
import vip.yazilim.p2g.web.exception.RoleException;
import vip.yazilim.p2g.web.exception.TokenException;
import vip.yazilim.p2g.web.model.UserModel;
import vip.yazilim.spring.utils.exception.DatabaseException;
import vip.yazilim.spring.utils.service.ICrudService;

import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IUserService extends ICrudService<User, String> {

    Optional<User> getUserByEmail(String email) throws DatabaseException;
    Optional<User> getUserByUuid(String uuid);
    Optional<UserModel> getUserModelByUserUuid(String userUuid) throws DatabaseException, RoleException;
    List<User> getUsersByRoomUuid(String roomUuid) throws DatabaseException;

    User setSpotifyInfo(com.wrapper.spotify.model_objects.specification.User spotifyUser, User user) throws DatabaseException, TokenException, RequestException;
}
