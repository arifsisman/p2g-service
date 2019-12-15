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
import java.util.HashMap;
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

    public static HashMap<String, List<Privileges>> rolePrivilegesList = new HashMap<>();

    @PostConstruct
    void init() throws DatabaseCreateException, DatabaseReadException {
        // Create roles
        Role p2gUserRole = roleService.createRole(Roles.P2G_USER.getRoleName());
        Role roomUserRole = roleService.createRole(Roles.ROOM_USER.getRoleName());
        Role roomModeratorRole = roleService.createRole(Roles.ROOM_MODERATOR.getRoleName());
        Role roomAdminRole = roleService.createRole(Roles.ROOM_ADMIN.getRoleName());
        Role roomOwnerRole = roleService.createRole(Roles.ROOM_OWNER.getRoleName());

        List<Privileges> p2gUserPrivileges = new LinkedList<>();
        p2gUserPrivileges.add(Privileges.ROOM_GET);
        p2gUserPrivileges.add(Privileges.ROOM_JOIN);
        p2gUserPrivileges.add(Privileges.ROOM_CREATE);
        p2gUserPrivileges.add(Privileges.ROOM_INVITE_REPLY);

        List<Privileges> roomUserPrivileges = new LinkedList<>();
        roomUserPrivileges.add(Privileges.SONG_LISTEN);

        List<Privileges> roomModeratorPrivileges = new LinkedList<>(roomUserPrivileges);
        roomModeratorPrivileges.add(Privileges.SONG_CONTROL);
        roomModeratorPrivileges.add(Privileges.ROOM_INVITE);

        List<Privileges> roomAdminPrivileges = new LinkedList<>(roomModeratorPrivileges);
        roomAdminPrivileges.add(Privileges.SONG_ADD);
        roomAdminPrivileges.add(Privileges.ROOM_MANAGE_ROLES);

        List<Privileges> roomOwnerPrivileges = new LinkedList<>(roomAdminPrivileges);
        roomOwnerPrivileges.add(Privileges.ROOM_DELETE);

        // Set privileges for roles
        privilegeService.setRolePrivileges(Roles.P2G_USER.getRoleName(), p2gUserPrivileges);
        privilegeService.setRolePrivileges(Roles.ROOM_USER.getRoleName(), roomUserPrivileges);
        privilegeService.setRolePrivileges(Roles.ROOM_MODERATOR.getRoleName(), roomModeratorPrivileges);
        privilegeService.setRolePrivileges(Roles.ROOM_ADMIN.getRoleName(), roomAdminPrivileges);
        privilegeService.setRolePrivileges(Roles.ROOM_OWNER.getRoleName(), roomOwnerPrivileges);

        // Set HashMap
        rolePrivilegesList.put(Roles.P2G_USER.getRoleName(), p2gUserPrivileges);
        rolePrivilegesList.put(Roles.ROOM_USER.getRoleName(), roomUserPrivileges);
        rolePrivilegesList.put(Roles.ROOM_MODERATOR.getRoleName(), roomModeratorPrivileges);
        rolePrivilegesList.put(Roles.ROOM_ADMIN.getRoleName(), roomAdminPrivileges);
        rolePrivilegesList.put(Roles.ROOM_OWNER.getRoleName(), roomOwnerPrivileges);
    }
}
