package vip.yazilim.p2g.web.service.p2g;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import vip.yazilim.p2g.web.constant.enums.Privilege;
import vip.yazilim.p2g.web.constant.enums.Role;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.model.UserModel;
import vip.yazilim.spring.core.exception.GeneralException;
import vip.yazilim.spring.core.exception.InvalidArgumentException;
import vip.yazilim.spring.core.exception.database.DatabaseException;
import vip.yazilim.spring.core.service.ICrudService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IUserService extends ICrudService<User, String> {

    Optional<User> getUserById(String id);
    UserModel getUserModelByUserId(String userId) throws DatabaseException, InvalidArgumentException;
    List<User> getUsersByRoomId(Long roomId) throws DatabaseException, InvalidArgumentException;

    User createUser(String id, String email, String username) throws GeneralException;

    User setSpotifyInfo(com.wrapper.spotify.model_objects.specification.User spotifyUser, User user) throws GeneralException, IOException, SpotifyWebApiException;

    // Rest
    boolean hasSystemRole(String userId, Role role) throws DatabaseException, InvalidArgumentException;

    boolean hasSystemPrivilege(String userId, Privilege privilege) throws DatabaseException, InvalidArgumentException;
}
