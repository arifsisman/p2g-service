package vip.yazilim.p2g.web.service;

import vip.yazilim.p2g.web.entity.Role;
import vip.yazilim.p2g.web.exception.RoleException;
import vip.yazilim.spring.utils.exception.DatabaseException;
import vip.yazilim.spring.utils.service.ICrudService;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IRoleService extends ICrudService<Role, String> {

    Role getDefaultRole() throws DatabaseException, RoleException;

}
