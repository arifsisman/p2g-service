package vip.yazilim.p2g.web.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.Role;
import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.entity.relation.RoomUser;
import vip.yazilim.p2g.web.exception.RoleException;
import vip.yazilim.p2g.web.model.UserModel;
import vip.yazilim.p2g.web.repository.IUserRepo;
import vip.yazilim.p2g.web.service.IRoleService;
import vip.yazilim.p2g.web.service.IRoomService;
import vip.yazilim.p2g.web.service.IUserService;
import vip.yazilim.p2g.web.service.relation.IRoomUserService;
import vip.yazilim.spring.utils.exception.DatabaseException;
import vip.yazilim.spring.utils.service.ACrudServiceImpl;

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
    private IRoomService roomService;

    @Autowired
    private IRoomUserService roomUserService;

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
    public Optional<UserModel> getUserModelByUuid(String userUuid) throws DatabaseException, RoleException {
        UserModel userModel = new UserModel();
        Optional<User> user;
        Optional<Role> role;
        String roomUuid;

        try {
            user = userRepo.findByUuid(userUuid);
        } catch (Exception exception) {
            String errorMessage = String.format("An error occurred while getting UserModel with userUuid[%s]", userUuid);
            throw new DatabaseException(errorMessage, exception);
        }

        // Set User
        if (!user.isPresent()) {
            return Optional.empty();
        } else {
            userModel.setUser(user.get());
        }

        // Room & Role
        Optional<Room> room = roomService.getRoomByUserUuid(userUuid);

        // If user joined a room, user has got a room and role
        // Else user is not in a room, user hasn't got a room and has got default role
        if (room.isPresent()) {
            userModel.setRoom(room.get());

            roomUuid = room.get().getUuid();
            role = roleService.getRoleByRoomAndUser(roomUuid, userUuid);

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
        List<RoomUser> roomUserList;

        try {
            userList = new ArrayList<>();

            roomUserList = roomUserService.getRoomUsersByRoomUuid(roomUuid);

            for (RoomUser roomUser : roomUserList) {
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
