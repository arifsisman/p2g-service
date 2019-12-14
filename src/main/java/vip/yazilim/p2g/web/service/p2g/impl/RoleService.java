package vip.yazilim.p2g.web.service.p2g.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.constant.Roles;
import vip.yazilim.p2g.web.entity.Role;
import vip.yazilim.p2g.web.entity.relation.RoomUser;
import vip.yazilim.p2g.web.exception.RoleException;
import vip.yazilim.p2g.web.exception.RoomException;
import vip.yazilim.p2g.web.exception.UserException;
import vip.yazilim.p2g.web.repository.IRoleRepo;
import vip.yazilim.p2g.web.service.p2g.IRoleService;
import vip.yazilim.p2g.web.service.p2g.relation.IPrivilegeService;
import vip.yazilim.p2g.web.service.p2g.relation.IRoomUserService;
import vip.yazilim.p2g.web.util.SecurityHelper;
import vip.yazilim.spring.core.exception.general.InvalidArgumentException;
import vip.yazilim.spring.core.exception.general.InvalidUpdateException;
import vip.yazilim.spring.core.exception.general.database.DatabaseCreateException;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;
import vip.yazilim.spring.core.service.ACrudServiceImpl;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
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

    @Autowired
    private IPrivilegeService privilegeService;

    @Override
    protected JpaRepository<Role, String> getRepository() {
        return roleRepo;
    }

    @Override
    protected String getId(Role entity) {
        return entity.getName();
    }

    @Override
    public Optional<Role> getRoleByRoomAndUser(String roomUuid, String userUuid) throws DatabaseException, InvalidArgumentException, RoomException {
        RoomUser roomUser = getRoomUser(userUuid);
        String roleName = roomUser.getRoleName();
        return getById(roleName);
    }

    @Override
    public String[] promoteUserRole(String roomUserUuid) throws DatabaseException, InvalidUpdateException, InvalidArgumentException, UserException, RoomException {
        RoomUser roomUser = getRoomUser(roomUserUuid);
        String roleName = roomUser.getRoleName();

        //todo: linkedhashmap get next
        if (roleName.equals(Roles.ROOM_USER.getRoleName())) {
            roleName = Roles.ROOM_MODERATOR.getRoleName();
        } else if (roleName.equals(Roles.ROOM_MODERATOR.getRoleName())) {
            roleName = Roles.ROOM_ADMIN.getRoleName();
        }

        roomUser.setRoleName(roleName);
        roomUserService.update(roomUser);

        return updateUserPrivileges(roomUser.getUserUuid());
    }

    @Override
    public String[] demoteUserRole(String roomUserUuid) throws DatabaseException, InvalidUpdateException, InvalidArgumentException, UserException, RoomException {
        RoomUser roomUser = getRoomUser(roomUserUuid);
        String roleName = roomUser.getRoleName();

        if (roleName.equals(Roles.ROOM_MODERATOR.getRoleName())) {
            roleName = Roles.ROOM_USER.getRoleName();
        } else if (roleName.equals(Roles.ROOM_ADMIN.getRoleName())) {
            roleName = Roles.ROOM_MODERATOR.getRoleName();
        }

        roomUser.setRoleName(roleName);
        roomUserService.update(roomUser);

        return updateUserPrivileges(roomUser.getUserUuid());
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

    private RoomUser getRoomUser(String roomUserUuid) throws RoomException, DatabaseException, InvalidArgumentException {
        Optional<RoomUser> roomUserOpt = roomUserService.getById(roomUserUuid);

        if (roomUserOpt.isPresent()) {
            return roomUserOpt.get();
        } else {
            throw new RoomException("User not in any room.");
        }
    }

    private String[] updateUserPrivileges(String userUuid) throws DatabaseException, UserException {
        List<GrantedAuthority> updatedAuthorities = new ArrayList<>();
        String[] privileges = privilegeService.setUserPrivileges(userUuid);

        for (String p : privileges) {
            updatedAuthorities.add(new SimpleGrantedAuthority(p));
        }

        Authentication auth = SecurityHelper.getUserAuthentication();
        Authentication newAuth = new UsernamePasswordAuthenticationToken(auth.getPrincipal(), auth.getCredentials(), updatedAuthorities);
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        return privileges;
    }

}
