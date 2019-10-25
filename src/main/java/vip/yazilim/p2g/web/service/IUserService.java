package vip.yazilim.p2g.web.service;

import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.model.UserModel;

import java.util.Optional;

/**
 * @author Emre Sen - 24.05.2019
 * @contact maemresen07@gmail.com
 */
public interface IUserService {

    Optional<User> getUserByEmail(String email);
    Optional<UserModel> getSystemUserModelByUuid(String systemUserUuid) throws Exception;

}
