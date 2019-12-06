package vip.yazilim.p2g.web.service.p2g;

import vip.yazilim.p2g.web.entity.Role;
import vip.yazilim.p2g.web.exception.RoleException;
import vip.yazilim.p2g.web.exception.RoomException;
import vip.yazilim.spring.core.exception.InvalidArgumentException;
import vip.yazilim.spring.core.exception.InvalidUpdateException;
import vip.yazilim.spring.core.exception.database.DatabaseException;
import vip.yazilim.spring.core.service.ICrudService;

import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IRoleService extends ICrudService<Role, String> {

    Optional<Role> getRoleByRoomAndUser(String roomUuid, String userUuid) throws DatabaseException, RoomException, InvalidArgumentException;

    String promoteUserRole(String userUuid) throws DatabaseException, RoomException, InvalidUpdateException, InvalidArgumentException;
    String demoteUserRole(String userUuid) throws DatabaseException, RoomException, InvalidUpdateException, InvalidArgumentException;

    Role getDefaultRole() throws DatabaseException, RoleException, InvalidArgumentException;

}
