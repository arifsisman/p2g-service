package vip.yazilim.p2g.web.service;

import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.model.UserModel;

import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IUserService extends CrudService<User, String> {

    Optional<User> getUserByEmail(String email);
    Optional<UserModel> getUserModelByUuid(String userUuid) throws Exception;

}
