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
import vip.yazilim.p2g.web.exception.RoomException;
import vip.yazilim.p2g.web.repository.IRoleRepo;
import vip.yazilim.p2g.web.service.p2g.IRoleService;
import vip.yazilim.p2g.web.service.p2g.relation.IRoomUserService;
import vip.yazilim.spring.core.exception.InvalidArgumentException;
import vip.yazilim.spring.core.exception.InvalidUpdateException;
import vip.yazilim.spring.core.exception.database.DatabaseException;
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
    public Optional<Role> getRoleByRoomAndUser(String roomUuid, String userUuid) throws DatabaseException, RoomException, InvalidArgumentException {
        Optional<RoomUser> roomUserOpt = roomUserService.getRoomUser(roomUuid, userUuid);

        if (!roomUserOpt.isPresent()) {
            String err = String.format("user[%s] not in room[%s]", userUuid, roomUuid);
            throw new RoomException(err);
        }

        RoomUser roomUser = roomUserOpt.get();
        String roleName = roomUser.getRoleName();
        return getById(roleName);
    }

    @Override
    public String promoteUserRole(String userUuid) throws DatabaseException, RoomException, InvalidUpdateException, InvalidArgumentException {
        RoomUser roomUser = getRoomUser(userUuid);
        String roleName = roomUser.getRoleName();

        if (roleName.equals(Roles.USER.getRoleName())) {
            roleName = Roles.MODERATOR.getRoleName();
        } else if (roleName.equals(Roles.MODERATOR.getRoleName())) {
            roleName = Roles.ADMIN.getRoleName();
        }

        roomUser.setRoleName(roleName);
        roomUserService.update(roomUser);

        return roleName;
    }

    @Override
    public String demoteUserRole(String userUuid) throws DatabaseException, RoomException, InvalidUpdateException, InvalidArgumentException {
        RoomUser roomUser = getRoomUser(userUuid);
        String roleName = roomUser.getRoleName();

        if (roleName.equals(Roles.MODERATOR.getRoleName())) {
            roleName = Roles.USER.getRoleName();
        } else if (roleName.equals(Roles.ADMIN.getRoleName())) {
            roleName = Roles.MODERATOR.getRoleName();
        }

        roomUser.setRoleName(roleName);
        roomUserService.update(roomUser);

        return roleName;
    }

    private RoomUser getRoomUser(String userUuid) throws DatabaseException, RoomException {
        Optional<RoomUser> roomUserOpt = roomUserService.getRoomUser(userUuid);

        if (roomUserOpt.isPresent()) {
            return roomUserOpt.get();
        } else {
            String err = String.format("user[%s] not in any room.", userUuid);
            throw new RoomException(err);
        }
    }

    @Override
    public Role getDefaultRole() throws DatabaseException, RoleException, InvalidArgumentException {
        Optional<Role> role = getById(Roles.USER.getRoleName());
        if (role.isPresent()) {
            return role.get();
        } else {
            throw new RoleException("Role not found!");
        }
    }
}
