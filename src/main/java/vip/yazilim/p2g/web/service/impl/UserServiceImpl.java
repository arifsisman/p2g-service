package vip.yazilim.p2g.web.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.Role;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.entity.relation.RoomUser;
import vip.yazilim.p2g.web.exception.DatabaseException;
import vip.yazilim.p2g.web.model.UserModel;
import vip.yazilim.p2g.web.repository.IUserRepo;
import vip.yazilim.p2g.web.repository.relation.IRoomUserRepo;
import vip.yazilim.p2g.web.service.IRoleService;
import vip.yazilim.p2g.web.service.IUserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class UserServiceImpl extends ACrudServiceImpl<User, String> implements IUserService {

    // static fields
    private Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    // injected dependencies
    @Autowired
    private IUserRepo userRepo;

    @Autowired
    private IRoleService roleService;

    @Autowired
    private IRoomUserRepo roomUserRepo;

    @Override
    protected JpaRepository<User, String> getRepository() {
        return userRepo;
    }

    @Override
    protected String getId(User entity) {
        return entity.getUuid();
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    @Override
    public Optional<UserModel> getUserModelByUuid(String userUuid) throws DatabaseException {
        Optional<User> user;

        try {
            user = userRepo.findByUuid(userUuid);
        } catch (Exception exception) {
            String errorMessage = String.format("An error occurred while getting UserModel with userUuid[%s]", userUuid);
            throw new DatabaseException(errorMessage, exception);
        }

        if (!user.isPresent()) {
            return Optional.empty();
        }

        UserModel userModel = new UserModel();
        userModel.setUser(user.get());

        // User Role
        Optional<Role> role = roleService.getRoleByUserUuid(userUuid);
        if (role.isPresent()) {
            userModel.setRole(role.get());
        } else {
            Role defaultRole = roleService.getDefaultRole();
            userModel.setRole(defaultRole);
        }

        return Optional.of(userModel);
    }

    @Override
    public List<User> getUsersByRoomUuid(String roomUuid) throws DatabaseException {
        String userUuid;
        List<User> userList;
        Iterable<RoomUser> roomUserIterable;

        try {
            userList = new ArrayList<>();

            roomUserIterable = roomUserRepo.findByRoomUuid(roomUuid);

            for (RoomUser roomUser : roomUserIterable) {
                userUuid = roomUser.getUuid();
                userList.add(userRepo.findByUuid(userUuid).get());
            }

        } catch (Exception exception) {
            String errorMessage = String.format("An error occurred while getting Users with roomUuid[%s]", roomUuid);
            throw new DatabaseException(errorMessage, exception);
        }

        return userList;
    }

}
