package vip.yazilim.p2g.web.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vip.yazilim.p2g.web.constant.Privileges;
import vip.yazilim.p2g.web.constant.Roles;
import vip.yazilim.p2g.web.entity.Role;
import vip.yazilim.p2g.web.service.p2g.IRoleService;
import vip.yazilim.p2g.web.service.p2g.relation.IPrivilegeService;
import vip.yazilim.spring.core.exception.general.database.DatabaseCreateException;
import vip.yazilim.spring.core.exception.general.database.DatabaseReadException;

import javax.annotation.PostConstruct;

/**
 * @author mustafaarifsisman - 12.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Component
public class AuthorizationInitializer {

    @Autowired
    private IRoleService roleService;

    @Autowired
    private IPrivilegeService privilegeService;

    @PostConstruct
    void init() throws DatabaseCreateException, DatabaseReadException {
        // Create roles
        Role p2gUserRole = roleService.createRole(Roles.P2G_USER.getRoleName());
        Role roomUserRole = roleService.createRole(Roles.ROOM_USER.getRoleName());
        Role roomModeratorRole = roleService.createRole(Roles.ROOM_MODERATOR.getRoleName());
        Role roomAdminRole = roleService.createRole(Roles.ROOM_ADMIN.getRoleName());

        // Set privileges for roles
        privilegeService.setRolePrivileges(Roles.P2G_USER.getRoleName(), Privileges.JOIN_ROOM, Privileges.CREATE_ROOM);

        privilegeService.setRolePrivileges(Roles.ROOM_USER.getRoleName(), Privileges.LISTEN_SONG);
        privilegeService.setRolePrivileges(Roles.ROOM_MODERATOR.getRoleName(), Privileges.LISTEN_SONG, Privileges.CONTROL_SONG, Privileges.INVITE_ROOM);
        privilegeService.setRolePrivileges(Roles.ROOM_ADMIN.getRoleName(), Privileges.LISTEN_SONG, Privileges.CONTROL_SONG, Privileges.INVITE_ROOM, Privileges.DELETE_ROOM, Privileges.ADD_SONG);
    }
}
