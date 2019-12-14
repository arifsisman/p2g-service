package vip.yazilim.p2g.web.service.p2g.relation;

import vip.yazilim.p2g.web.constant.Privileges;
import vip.yazilim.p2g.web.entity.Privilege;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.exception.UserException;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;
import vip.yazilim.spring.core.exception.general.database.DatabaseReadException;
import vip.yazilim.spring.core.service.ICrudService;

import java.util.List;

/**
 * @author mustafaarifsisman - 12.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IPrivilegeService extends ICrudService<Privilege, String> {

    List<Privilege> setRolePrivileges(String roleName, List<Privileges> privilegeList) throws DatabaseReadException;
    List<Privilege> getRolePrivileges(String roleName) throws DatabaseReadException;

    String[] setUserPrivileges(User user) throws DatabaseException;
    String[] setUserPrivileges(String userUuid) throws DatabaseException, UserException;
}
