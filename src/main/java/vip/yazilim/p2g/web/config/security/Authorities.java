package vip.yazilim.p2g.web.config.security;

import org.springframework.stereotype.Component;
import vip.yazilim.p2g.web.constant.Privilege;
import vip.yazilim.p2g.web.constant.Role;

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
    protected HashMap<Role, List<Privilege>> initPrivileges() {
        HashMap<Role, List<Privilege>> rolePrivilegesMap = new HashMap<>();

        List<Privilege> p2gUserPrivileges = new LinkedList<>();
        p2gUserPrivileges.add(Privilege.ROOM_GET);
        p2gUserPrivileges.add(Privilege.ROOM_JOIN);
        p2gUserPrivileges.add(Privilege.ROOM_CREATE);
        p2gUserPrivileges.add(Privilege.ROOM_INVITE_REPLY);

        List<Privilege> roomUserPrivileges = new LinkedList<>(p2gUserPrivileges);
        roomUserPrivileges.add(Privilege.SONG_LISTEN);

        List<Privilege> roomModeratorPrivileges = new LinkedList<>(roomUserPrivileges);
        roomModeratorPrivileges.add(Privilege.SONG_CONTROL);
        roomModeratorPrivileges.add(Privilege.ROOM_INVITE);

        List<Privilege> roomAdminPrivileges = new LinkedList<>(roomModeratorPrivileges);
        roomAdminPrivileges.add(Privilege.SONG_ADD);
        roomAdminPrivileges.add(Privilege.ROOM_MANAGE_ROLES);

        List<Privilege> roomOwnerPrivileges = new LinkedList<>(roomAdminPrivileges);
        roomOwnerPrivileges.add(Privilege.ROOM_DELETE);

        // Set HashMap
        rolePrivilegesMap.put(Role.P2G_USER, p2gUserPrivileges);
        rolePrivilegesMap.put(Role.ROOM_USER, roomUserPrivileges);
        rolePrivilegesMap.put(Role.ROOM_MODERATOR, roomModeratorPrivileges);
        rolePrivilegesMap.put(Role.ROOM_ADMIN, roomAdminPrivileges);
        rolePrivilegesMap.put(Role.ROOM_OWNER, roomOwnerPrivileges);

        return rolePrivilegesMap;
    }
}
