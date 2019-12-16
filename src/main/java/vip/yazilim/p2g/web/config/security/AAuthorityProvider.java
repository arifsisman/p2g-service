package vip.yazilim.p2g.web.config.security;

import vip.yazilim.p2g.web.constant.Privilege;
import vip.yazilim.p2g.web.constant.Role;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;

/**
 * @author mustafaarifsisman - 16.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
public abstract class AAuthorityProvider {

    private HashMap<Role, List<Privilege>> rolePrivilegesMap;

    @PostConstruct
    void initMap() {
        rolePrivilegesMap = initPrivileges();
    }

    /**
     * P2G User       -> ROOM_GET, ROOM_JOIN, ROOM_CREATE, ROOM_INVITE_REPLY
     * Room User      -> (P2G User) + SONG_LISTEN
     * Room Moderator -> (Room User) + SONG_CONTROL, ROOM_INVITE
     * Room Admin     -> (Room Moderator) + SONG_ADD, ROOM_MANAGE_ROLES
     * Room Owner     -> (Room Admin) + ROOM_DELETE
     * @return map of roles and privileges
     */
    protected abstract HashMap<Role, List<Privilege>> initPrivileges();

    public List<Privilege> getPrivilegeListByRoleName(Role roleName) {
        return rolePrivilegesMap.get(roleName);
    }

    public boolean hasPrivilege(Role role, Privilege privilege) {
        List<Privilege> privilegeList = getPrivilegeListByRoleName(role);
        return privilegeList != null && privilegeList.contains(privilege);
    }

    public boolean hasRole(Role role) {
        return rolePrivilegesMap.containsKey(role);
    }
}
