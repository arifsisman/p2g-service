package vip.yazilim.p2g.web.service.p2g.relation;

import vip.yazilim.p2g.web.constant.Privileges;
import vip.yazilim.p2g.web.entity.Privilege;
import vip.yazilim.spring.core.exception.general.database.DatabaseReadException;
import vip.yazilim.spring.core.service.ICrudService;

import java.util.List;

/**
 * @author mustafaarifsisman - 12.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IPrivilegeService extends ICrudService<Privilege, String> {

    List<Privilege> setPrivilegeList(String roleName, Privileges... privileges) throws DatabaseReadException;
    List<Privilege> getPrivilegeList(String roleName) throws DatabaseReadException;

}
