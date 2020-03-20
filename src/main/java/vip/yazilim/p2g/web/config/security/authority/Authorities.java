package vip.yazilim.p2g.web.config.security.authority;

import org.springframework.stereotype.Component;
import vip.yazilim.p2g.web.enums.Privilege;
import vip.yazilim.p2g.web.enums.Role;

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
    protected HashMap<String, List<Privilege>> initPrivileges() {
        HashMap<String, List<Privilege>> rolePrivilegesMap = new HashMap<>();

        List<Privilege> p2gUserPrivileges = new LinkedList<>();
        p2gUserPrivileges.add(Privilege.ROOM_CREATE);
        p2gUserPrivileges.add(Privilege.ROOM_JOIN_AND_LEAVE);
        p2gUserPrivileges.add(Privilege.ROOM_INVITE_AND_REPLY);

        List<Privilege> roomUserPrivileges = new LinkedList<>(p2gUserPrivileges);
        roomUserPrivileges.add(Privilege.SONG_GET);
        roomUserPrivileges.add(Privilege.SONG_VOTE);

        List<Privilege> roomDjPrivileges = new LinkedList<>(roomUserPrivileges);
        roomDjPrivileges.add(Privilege.ROOM_CLEAR_QUEUE);
        roomDjPrivileges.add(Privilege.SONG_ADD_AND_REMOVE);
        roomDjPrivileges.add(Privilege.SONG_CONTROL);
        roomDjPrivileges.add(Privilege.SONG_SEARCH);

        List<Privilege> roomAdminPrivileges = new LinkedList<>(roomDjPrivileges);
        roomAdminPrivileges.add(Privilege.ROOM_MANAGE_ROLES);

        List<Privilege> roomOwnerPrivileges = new LinkedList<>(roomAdminPrivileges);
        roomOwnerPrivileges.add(Privilege.ROOM_UPDATE);

        // Set HashMap
        rolePrivilegesMap.put(Role.P2G_USER.role, p2gUserPrivileges);
        rolePrivilegesMap.put(Role.ROOM_USER.role, roomUserPrivileges);
        rolePrivilegesMap.put(Role.ROOM_DJ.role, roomDjPrivileges);
        rolePrivilegesMap.put(Role.ROOM_ADMIN.role, roomAdminPrivileges);
        rolePrivilegesMap.put(Role.ROOM_OWNER.role, roomOwnerPrivileges);

        return rolePrivilegesMap;
    }
}
