package vip.yazilim.p2g.web.config.security.authority;

import vip.yazilim.p2g.web.constant.enums.Privilege;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;

/**
 * @author mustafaarifsisman - 16.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
public abstract class AAuthorityProvider {

    private HashMap<String, List<Privilege>> rolePrivilegesMap;

    @PostConstruct
    void initMap() {
        rolePrivilegesMap = initPrivileges();
    }

    /**
     * P2G User       -> ROOM_GET, ROOM_JOIN_AND_LEAVE, ROOM_CREATE, ROOM_INVITE_REPLY
     * Room User      -> (P2G User) + SONG_GET
     * Room Moderator -> (Room User) + SONG_CONTROL, ROOM_INVITE, SONG_UPDATE
     * Room Admin     -> (Room Moderator) + SONG_ADD_AND_REMOVE, ROOM_MANAGE_ROLES, ROOM_UPDATE
     * Room Owner     -> (Room Admin) + ROOM_DELETE
     *
     * @return map of roles and privileges
     */
    protected abstract HashMap<String, List<Privilege>> initPrivileges();

    public List<Privilege> getPrivilegeListByRoleName(String role) {
        return rolePrivilegesMap.get(role);
    }

    public boolean hasPrivilege(String role, Privilege privilege) {
        List<Privilege> privilegeList = getPrivilegeListByRoleName(role);
        return (privilegeList != null) && privilegeList.contains(privilege);
    }

    public boolean hasRole(String role) {
        return rolePrivilegesMap.containsKey(role);
    }
}
