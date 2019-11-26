package vip.yazilim.p2g.web.service.impl;

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
import vip.yazilim.p2g.web.service.IRoleService;
import vip.yazilim.p2g.web.service.relation.IRoomUserService;
import vip.yazilim.spring.utils.exception.DatabaseException;
import vip.yazilim.spring.utils.exception.InvalidUpdateException;
import vip.yazilim.spring.utils.service.ACrudServiceImpl;

import java.util.Optional;

/**
 * @author mustafaarifsisman - 1.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
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
    public Optional<Role> getRoleByRoomAndUser(String roomUuid, String userUuid) throws DatabaseException {
        Optional<RoomUser> roomUser = roomUserService.getRoomUser(roomUuid, userUuid);

        if (!roomUser.isPresent()) {
            return Optional.empty();
        }

        String roleName = roomUser.get().getRoleName();
        return getById(roleName);
    }

    @Override
    public String changeUserRole(String roomUuid, String userUuid, boolean rank) throws DatabaseException, RoleException {
        Optional<RoomUser> roomUserOpt = roomUserService.getRoomUser(roomUuid, userUuid);
        RoomUser roomUser;

        if (!roomUserOpt.isPresent()) {
            LOGGER.warn("User[{}] not in Room[{}]", userUuid, roomUuid);
            return getDefaultRole().getName();
        }else{
            roomUser = roomUserOpt.get();
        }

        String roleName = roomUser.getRoleName();

        if (rank) {
            switch (roleName) {
                case "USER":
                    roleName = Roles.MODERATOR.getRoleName();
                    break;
                case "MODERATOR":
                    roleName = Roles.ADMIN.getRoleName();
                    break;
            }
        } else {
            switch (roleName) {
                case "MODERATOR":
                    roleName = Roles.USER.getRoleName();
                    break;
                case "ADMIN":
                    roleName = Roles.MODERATOR.getRoleName();
                    break;
            }
        }

        roomUser.setRoleName(roleName);

        try {
            roomUserService.update(roomUser);
        } catch (InvalidUpdateException e) {
            e.printStackTrace();
        }

        return roleName;
    }

    @Override
    public Role getDefaultRole() throws DatabaseException, RoleException {
        Optional<Role> role = getById(Roles.USER.getRoleName());

        if (!role.isPresent()) {
            throw new RoleException("Role not found!");
        }

        return role.get();
    }
}
