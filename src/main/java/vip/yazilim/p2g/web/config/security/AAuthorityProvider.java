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
    void initMap(){
        rolePrivilegesMap = initAuthorities();
    }

    protected abstract HashMap<Roles, List<Privileges>> initAuthorities();

    public List<Privileges> getPrivilegeListByRoleName(Roles roleName){
        return rolePrivilegesMap.get(roleName);
    }

}
