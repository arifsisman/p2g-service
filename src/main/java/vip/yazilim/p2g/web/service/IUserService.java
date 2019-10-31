package vip.yazilim.p2g.web.service;

import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.exception.DatabaseException;
import vip.yazilim.p2g.web.model.UserModel;

import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IUserService extends ICrudService<User, String> {

    Optional<User> getUserByEmail(String email) throws DatabaseException;
    Optional<UserModel> getUserModelByUuid(String userUuid) throws DatabaseException;
    List<User> getUsersByRoomUuid(String roomUuid) throws DatabaseException;

}
