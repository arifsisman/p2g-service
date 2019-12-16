package vip.yazilim.p2g.web.config.security;

import vip.yazilim.p2g.web.constant.Privileges;
import vip.yazilim.p2g.web.constant.Roles;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;

/**
 * @author mustafaarifsisman - 16.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
public abstract class AAuthorityProvider {

    private HashMap<Roles, List<Privileges>> rolePrivilegesMap;

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
    protected abstract HashMap<Roles, List<Privileges>> initPrivileges();

    public List<Privileges> getPrivilegeListByRoleName(Roles roleName) {
        return rolePrivilegesMap.get(roleName);
    }

    public boolean hasPrivilege(Roles roles, Privileges privileges) {
        List<Privileges> privilegesList = getPrivilegeListByRoleName(roles);
        return privilegesList != null && privilegesList.contains(privileges);
    }

    public boolean hasRole(Roles roles) {
        return rolePrivilegesMap.containsKey(roles);
    }
}
