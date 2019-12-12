package vip.yazilim.p2g.web.service.p2g.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.constant.Roles;
import vip.yazilim.p2g.web.entity.Role;
import vip.yazilim.p2g.web.entity.relation.RoomUser;
import vip.yazilim.p2g.web.exception.RoleException;
import vip.yazilim.p2g.web.repository.IRoleRepo;
import vip.yazilim.p2g.web.service.p2g.IRoleService;
import vip.yazilim.p2g.web.service.p2g.relation.IRoomUserService;
import vip.yazilim.spring.core.exception.general.InvalidArgumentException;
import vip.yazilim.spring.core.exception.general.InvalidUpdateException;
import vip.yazilim.spring.core.exception.general.database.DatabaseCreateException;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;
import vip.yazilim.spring.core.service.ACrudServiceImpl;

import javax.transaction.Transactional;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 1.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Transactional
@Service
public class RoleService extends ACrudServiceImpl<Role, String> implements IRoleService {

    // static fields
    private Logger LOGGER = LoggerFactory.getLogger(RoleService.class);

    // injected dependencies
    @Autowired
    private IRoleRepo roleRepo;

    @Autowired
    private IRoomUserService roomUserService;

    @Override
    protected JpaRepository<Role, String> getRepository() {
        return roleRepo;
    }

    @Override
    protected String getId(Role entity) {
        return entity.getName();
    }

    @Override
    public Optional<Role> getRoleByRoomAndUser(String roomUuid, String userUuid) throws DatabaseException, InvalidArgumentException {
        RoomUser roomUser = roomUserService.getRoomUser(roomUuid, userUuid);
        String roleName = roomUser.getRoleName();
        return getById(roleName);
    }

    @Override
    public String promoteUserRole(String userUuid) throws DatabaseException, InvalidUpdateException, InvalidArgumentException {
        RoomUser roomUser = roomUserService.getRoomUser(userUuid);
        String roleName = roomUser.getRoleName();

        if (roleName.equals(Roles.ROOM_USER.getRoleName())) {
            roleName = Roles.ROOM_MODERATOR.getRoleName();
        } else if (roleName.equals(Roles.ROOM_MODERATOR.getRoleName())) {
            roleName = Roles.ROOM_ADMIN.getRoleName();
        }

        roomUser.setRoleName(roleName);
        roomUserService.update(roomUser);

        return roleName;
    }

    @Override
    public String demoteUserRole(String userUuid) throws DatabaseException, InvalidUpdateException, InvalidArgumentException {
        RoomUser roomUser = roomUserService.getRoomUser(userUuid);
        String roleName = roomUser.getRoleName();

        if (roleName.equals(Roles.ROOM_MODERATOR.getRoleName())) {
            roleName = Roles.ROOM_USER.getRoleName();
        } else if (roleName.equals(Roles.ROOM_ADMIN.getRoleName())) {
            roleName = Roles.ROOM_MODERATOR.getRoleName();
        }

        roomUser.setRoleName(roleName);
        roomUserService.update(roomUser);

        return roleName;
    }

    @Override
    public Role getDefaultRole() throws DatabaseException, RoleException, InvalidArgumentException {
        Optional<Role> role = getById(Roles.ROOM_USER.getRoleName());
        if (role.isPresent()) {
            return role.get();
        } else {
            throw new RoleException("Role not found!");
        }
    }

    @Override
    public Role createRole(String roleName) throws DatabaseCreateException {
        Role role = new Role();
        role.setName(roleName);

        try {
            return create(role);
        } catch (Exception e) {
            throw new DatabaseCreateException(e);
        }
    }

}
