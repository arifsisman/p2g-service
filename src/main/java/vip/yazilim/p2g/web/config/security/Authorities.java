package vip.yazilim.p2g.web.config.security;

import org.springframework.stereotype.Component;
import vip.yazilim.p2g.web.constant.Privileges;
import vip.yazilim.p2g.web.constant.Roles;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * @author mustafaarifsisman - 12.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Component
public class Authorities extends AAuthorityProvider {

    @Override
    protected HashMap<Roles, List<Privileges>> initPrivileges() {
        HashMap<Roles, List<Privileges>> rolePrivilegesMap = new HashMap<>();

        List<Privileges> p2gUserPrivileges = new LinkedList<>();
        p2gUserPrivileges.add(Privileges.ROOM_GET);
        p2gUserPrivileges.add(Privileges.ROOM_JOIN);
        p2gUserPrivileges.add(Privileges.ROOM_CREATE);
        p2gUserPrivileges.add(Privileges.ROOM_INVITE_REPLY);

        List<Privileges> roomUserPrivileges = new LinkedList<>(p2gUserPrivileges);
        roomUserPrivileges.add(Privileges.SONG_LISTEN);

        List<Privileges> roomModeratorPrivileges = new LinkedList<>(roomUserPrivileges);
        roomModeratorPrivileges.add(Privileges.SONG_CONTROL);
        roomModeratorPrivileges.add(Privileges.ROOM_INVITE);

        List<Privileges> roomAdminPrivileges = new LinkedList<>(roomModeratorPrivileges);
        roomAdminPrivileges.add(Privileges.SONG_ADD);
        roomAdminPrivileges.add(Privileges.ROOM_MANAGE_ROLES);

        List<Privileges> roomOwnerPrivileges = new LinkedList<>(roomAdminPrivileges);
        roomOwnerPrivileges.add(Privileges.ROOM_DELETE);

        // Set HashMap
        rolePrivilegesMap.put(Roles.P2G_USER, p2gUserPrivileges);
        rolePrivilegesMap.put(Roles.ROOM_USER, roomUserPrivileges);
        rolePrivilegesMap.put(Roles.ROOM_MODERATOR, roomModeratorPrivileges);
        rolePrivilegesMap.put(Roles.ROOM_ADMIN, roomAdminPrivileges);
        rolePrivilegesMap.put(Roles.ROOM_OWNER, roomOwnerPrivileges);

        return rolePrivilegesMap;
    }
}
