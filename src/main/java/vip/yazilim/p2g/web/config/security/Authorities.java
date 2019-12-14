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
import java.util.LinkedList;
import java.util.List;

/**
 * @author mustafaarifsisman - 12.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Component
public class Authorities {

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
        Role roomOwnerRole = roleService.createRole(Roles.ROOM_OWNER.getRoleName());

        //todo: make lists static
        // Init privilege lists
        List<Privileges> p2gUserPrivileges = new LinkedList<>();
        p2gUserPrivileges.add(Privileges.GET_ROOM);
        p2gUserPrivileges.add(Privileges.JOIN_ROOM);
        p2gUserPrivileges.add(Privileges.CREATE_ROOM);

        List<Privileges> roomUserPrivileges = new LinkedList<>();
        roomUserPrivileges.add(Privileges.LISTEN_SONG);

        List<Privileges> roomModeratorPrivileges = new LinkedList<>(roomUserPrivileges);
        roomModeratorPrivileges.add(Privileges.CONTROL_SONG);
        roomModeratorPrivileges.add(Privileges.INVITE_ROOM);

        List<Privileges> roomAdminPrivileges = new LinkedList<>(roomModeratorPrivileges);
        roomAdminPrivileges.add(Privileges.ADD_SONG);

        List<Privileges> roomOwnerPrivileges = new LinkedList<>(roomAdminPrivileges);
        roomOwnerPrivileges.add(Privileges.DELETE_ROOM);

        // Set privileges for roles
        privilegeService.setRolePrivileges(Roles.P2G_USER.getRoleName(), p2gUserPrivileges);
        privilegeService.setRolePrivileges(Roles.ROOM_USER.getRoleName(), roomUserPrivileges);
        privilegeService.setRolePrivileges(Roles.ROOM_MODERATOR.getRoleName(), roomModeratorPrivileges);
        privilegeService.setRolePrivileges(Roles.ROOM_ADMIN.getRoleName(), roomAdminPrivileges);
        privilegeService.setRolePrivileges(Roles.ROOM_OWNER.getRoleName(), roomOwnerPrivileges);
    }
}
