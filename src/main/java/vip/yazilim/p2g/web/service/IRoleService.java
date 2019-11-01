package vip.yazilim.p2g.web.service;

import vip.yazilim.p2g.web.entity.Role;
import vip.yazilim.p2g.web.exception.DatabaseException;

import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IRoleService extends ICrudService<Role, String> {

    Optional<Role> getRoleByUserUuid(String userUuid) throws DatabaseException;
    Role getIdleRole();
    Role getDefaultRole();

}
