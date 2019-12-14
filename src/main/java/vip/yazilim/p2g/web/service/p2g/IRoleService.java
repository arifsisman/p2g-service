package vip.yazilim.p2g.web.service.p2g;

import vip.yazilim.p2g.web.entity.Role;
import vip.yazilim.p2g.web.entity.relation.RoomUser;
import vip.yazilim.p2g.web.exception.RoleException;
import vip.yazilim.p2g.web.exception.RoomException;
import vip.yazilim.p2g.web.exception.UserException;
import vip.yazilim.spring.core.exception.general.InvalidArgumentException;
import vip.yazilim.spring.core.exception.general.InvalidUpdateException;
import vip.yazilim.spring.core.exception.general.database.DatabaseCreateException;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;
import vip.yazilim.spring.core.service.ICrudService;

import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IRoleService extends ICrudService<Role, String> {

    Optional<Role> getRoleByRoomAndUser(String roomUuid, String userUuid) throws DatabaseException, RoomException, InvalidArgumentException;

    String[] promoteUserRole(RoomUser roomUser) throws DatabaseException, InvalidUpdateException, InvalidArgumentException, UserException;
    String[] demoteUserRole(RoomUser roomUser) throws DatabaseException, InvalidUpdateException, InvalidArgumentException, UserException;

    Role getDefaultRole() throws DatabaseException, RoleException, InvalidArgumentException;
    Role createRole(String roleName) throws DatabaseCreateException;

}
