package vip.yazilim.p2g.web.service.p2g;

import vip.yazilim.libs.springcore.exception.general.BusinessLogicException;
import vip.yazilim.libs.springcore.exception.general.InvalidArgumentException;
import vip.yazilim.libs.springcore.exception.general.database.DatabaseException;
import vip.yazilim.libs.springcore.service.ICrudService;
import vip.yazilim.p2g.web.constant.enums.Privilege;
import vip.yazilim.p2g.web.constant.enums.Role;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.model.UserModel;

import java.util.List;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IUserService extends ICrudService<User, String> {

    UserModel getUserModelByUserId(String userId) throws DatabaseException, InvalidArgumentException;

    List<User> getUsersByRoomId(Long roomId) throws DatabaseException, InvalidArgumentException;

    User createUser(String id, String email, String username) throws BusinessLogicException;

    User setSpotifyInfo(com.wrapper.spotify.model_objects.specification.User spotifyUser, User user) throws BusinessLogicException;

    boolean hasSystemRole(String userId, Role role) throws DatabaseException, InvalidArgumentException;

    boolean hasSystemPrivilege(String userId, Privilege privilege) throws DatabaseException, InvalidArgumentException;
}
