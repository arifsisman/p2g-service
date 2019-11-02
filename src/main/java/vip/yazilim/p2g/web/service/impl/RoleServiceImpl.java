package vip.yazilim.p2g.web.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.constant.Roles;
import vip.yazilim.p2g.web.entity.Role;
import vip.yazilim.p2g.web.entity.relation.RoomUser;
import vip.yazilim.p2g.web.exception.DatabaseException;
import vip.yazilim.p2g.web.repository.IRoleRepo;
import vip.yazilim.p2g.web.repository.relation.IRoomUserRepo;
import vip.yazilim.p2g.web.service.IRoleService;
import vip.yazilim.p2g.web.util.DBHelper;
import vip.yazilim.spring.utils.service.ACrudServiceImpl;

import java.util.Optional;

/**
 * @author mustafaarifsisman - 1.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class RoleServiceImpl extends ACrudServiceImpl<Role, String> implements IRoleService {

    // static fields
    private Logger LOGGER = LoggerFactory.getLogger(RoleServiceImpl.class);

    // injected dependencies
    @Autowired
    private IRoleRepo roleRepo;

    @Autowired
    private IRoomUserRepo roomUserRepo;

    @Override
    public Optional<Role> getRoleByUserUuid(String userUuid) throws DatabaseException {
        Optional<Role> roleOptional;
        Optional<RoomUser> roomUserOptional;

        try {
            roomUserOptional = roomUserRepo.findByUserUuid(userUuid);

            if (!roomUserOptional.isPresent()) {
                return Optional.of(getIdleRole());
            }

            String roleUuid = roomUserOptional.get().getRoleUuid();
            roleOptional = roleRepo.findByUuid(roleUuid);

        } catch (Exception exception) {
            String errorMessage = String.format("An error occurred while getting Role with userUuid[%s]", userUuid);
            throw new DatabaseException(errorMessage, exception);
        }

        return roleOptional;
    }

    @Override
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public Role getIdleRole() {
        Role idleRole = new Role();
        String randomUuid = DBHelper.getRandomUuid();
        idleRole.setUuid(randomUuid);
        idleRole.setName(String.valueOf(Roles.NOT_IN_ANY_ROOM));

        return idleRole;
    }

    @Override
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public Role getDefaultRole() {
        Role defaultRole = new Role();
        String randomUuid = DBHelper.getRandomUuid();
        defaultRole.setUuid(randomUuid);
        defaultRole.setName(String.valueOf(Roles.USER));

        return defaultRole;
    }

    @Override
    protected JpaRepository<Role, String> getRepository() {
        return roleRepo;
    }

    @Override
    protected String getId(Role entity) {
        return entity.getUuid();
    }
}
