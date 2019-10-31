package vip.yazilim.p2g.web.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import vip.yazilim.p2g.web.entity.Role;
import vip.yazilim.p2g.web.exception.DatabaseException;
import vip.yazilim.p2g.web.repository.IRoleRepo;
import vip.yazilim.p2g.web.repository.relation.IRoomUserRepo;
import vip.yazilim.p2g.web.service.IRoleService;

import java.util.Optional;

/**
 * @author mustafaarifsisman - 1.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
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

        //TODO: if user is not in room return not in any room role
        try {

        } catch (Exception exception) {
            String errorMessage = String.format("An error occurred while getting Role with userUuid[%s]", userUuid);
            throw new DatabaseException(errorMessage, exception);
        }

        return roleOptional;
    }

    @Override
    public Role getDefaultRole() {
        return null;
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
