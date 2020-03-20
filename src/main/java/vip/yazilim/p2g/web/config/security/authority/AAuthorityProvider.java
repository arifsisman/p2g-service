package vip.yazilim.p2g.web.config.security.authority;

import vip.yazilim.p2g.web.enums.Privilege;

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
     * P2G User       ->
     * Room User      -> (P2G User) + ...
     * Room Moderator -> (Room User) + ...
     * Room Admin     -> (Room Moderator) + ...
     * Room Owner     -> (Room Admin) + ...
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
