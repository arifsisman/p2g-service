package vip.yazilim.p2g.web.service;

import vip.yazilim.p2g.web.entity.Role;

import java.util.Optional;

/**
 * @author Emre Sen - 24.05.2019
 * @contact maemresen07@gmail.com
 */
public interface ISystemRoleService{

    Optional<Role> getSystemRoleByUuid(String systemUserUuid);
    Role getDefaultRole();
}
