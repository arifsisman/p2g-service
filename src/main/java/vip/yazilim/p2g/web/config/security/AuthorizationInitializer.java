package vip.yazilim.p2g.web.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vip.yazilim.p2g.web.constant.Privileges;
import vip.yazilim.p2g.web.constant.Roles;
import vip.yazilim.p2g.web.entity.Privilege;
import vip.yazilim.p2g.web.entity.Role;
import vip.yazilim.p2g.web.service.p2g.IRoleService;
import vip.yazilim.p2g.web.service.p2g.relation.IPrivilegeService;
import vip.yazilim.spring.core.exception.general.database.DatabaseCreateException;
import vip.yazilim.spring.core.exception.general.database.DatabaseReadException;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collections;

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

        // Create privileges
        Privilege joinRoom = createPrivilege(Privileges.JOIN_ROOM.getPrivilegeName());
        Privilege createRoom = createPrivilege(Privileges.CREATE_ROOM.getPrivilegeName());
        Privilege inviteRoom = createPrivilege(Privileges.INVITE_ROOM.getPrivilegeName());
        Privilege listenSong = createPrivilege(Privileges.LISTEN_SONG.getPrivilegeName());
        Privilege controlSong = createPrivilege(Privileges.CONTROL_SONG.getPrivilegeName());
        Privilege addSong = createPrivilege(Privileges.ADD_SONG.getPrivilegeName());

        // Set privileges for roles
        privilegeService.setPrivilegeList(Roles.P2G_USER.getRoleName(), Arrays.asList(joinRoom, createRoom));

        privilegeService.setPrivilegeList(Roles.ROOM_USER.getRoleName(), Collections.singletonList(listenSong));
        privilegeService.setPrivilegeList(Roles.ROOM_MODERATOR.getRoleName(), Arrays.asList(listenSong, controlSong, inviteRoom));
        privilegeService.setPrivilegeList(Roles.ROOM_ADMIN.getRoleName(), Arrays.asList(listenSong, controlSong, addSong, inviteRoom));
    }

    private Privilege createPrivilege(String privilegeName){
        Privilege privilege = new Privilege();
        privilege.setPrivilegeName(privilegeName);
        return privilege;
    }
}
