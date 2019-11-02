package vip.yazilim.p2g.web.service.impl.relation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.Role;
import vip.yazilim.p2g.web.entity.relation.RoomUser;
import vip.yazilim.p2g.web.repository.relation.IRoomUserRepo;
import vip.yazilim.p2g.web.service.IRoleService;
import vip.yazilim.p2g.web.service.relation.IRoomUserService;
import vip.yazilim.spring.utils.exception.DatabaseException;
import vip.yazilim.spring.utils.service.ACrudServiceImpl;

import java.util.Optional;

/**
 * @author mustafaarifsisman - 2.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class RoomUserServiceImpl extends ACrudServiceImpl<RoomUser, String> implements IRoomUserService {

    // static fields
    private Logger LOGGER = LoggerFactory.getLogger(RoomUserServiceImpl.class);

    // injected dependencies
    @Autowired
    private IRoomUserRepo roomUserRepo;

    @Autowired
    private IRoleService roleService;


    @Override
    protected JpaRepository<RoomUser, String> getRepository() {
        return roomUserRepo;
    }

    @Override
    protected String getId(RoomUser entity) {
        return entity.getUuid();
    }

    @Override
    public Optional<Role> getRoleByRoomAndUser(String roomUuid, String userUuid) throws DatabaseException {
        Optional<RoomUser> roomUser;

        try {
            roomUser = roomUserRepo.findByRoomUuidAndUserUuid(roomUuid, userUuid);

        } catch (Exception exception) {
            String errorMessage = String.format("An error occurred while getting Role with userUuid[%s]", userUuid);
            throw new DatabaseException(errorMessage, exception);
        }

        if(!roomUser.isPresent()){
            // TODO ....
        }

        String roleName = roomUser.get().getRoleName();
        return roleService.getById(roleName);
    }

}
