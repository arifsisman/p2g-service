package vip.yazilim.p2g.web.service.p2g.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.libs.springcore.exception.DatabaseReadException;
import vip.yazilim.libs.springcore.service.ACrudServiceImpl;
import vip.yazilim.p2g.web.config.security.authority.AAuthorityProvider;
import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.entity.RoomUser;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.enums.OnlineStatus;
import vip.yazilim.p2g.web.enums.Privilege;
import vip.yazilim.p2g.web.enums.Role;
import vip.yazilim.p2g.web.exception.ConstraintViolationException;
import vip.yazilim.p2g.web.model.UserModel;
import vip.yazilim.p2g.web.repository.IUserRepo;
import vip.yazilim.p2g.web.service.p2g.IRoomService;
import vip.yazilim.p2g.web.service.p2g.IRoomUserService;
import vip.yazilim.p2g.web.service.p2g.IUserService;
import vip.yazilim.p2g.web.util.TimeHelper;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class UserService extends ACrudServiceImpl<User, String> implements IUserService {

    @Autowired
    private IUserRepo userRepo;

    @Autowired
    private IRoomService roomService;

    @Autowired
    private IRoomUserService roomUserService;

    @Autowired
    private AAuthorityProvider authorityProvider;

    @Override
    protected JpaRepository<User, String> getRepository() {
        return userRepo;
    }

    @Override
    protected String getId(User entity) {
        return entity.getId();
    }

    @Override
    protected Class<User> getClassOfEntity() {
        return User.class;
    }

    @Override
    protected User preInsert(User entity) {
        entity.setCreationDate(TimeHelper.getLocalDateTimeNow());
        entity.setRole(Role.P2G_USER.getRole());
        entity.setOnlineStatus(OnlineStatus.ONLINE.getOnlineStatus());
        return entity;
    }

    @Override
    public UserModel getUserModelByUserId(String userId) {
        UserModel userModel = new UserModel();

        Optional<User> userOpt;
        Optional<Room> roomOpt;
        Optional<RoomUser> roomUser;

        // Set User
        userOpt = getById(userId);
        if (!userOpt.isPresent()) {
            throw new NoSuchElementException("User not found");
        } else {
            userModel.setUser(userOpt.get());
        }

        // Set Room & Role
        roomOpt = roomService.getRoomByUserId(userId);

        // If user joined a room, user has got a room and role
        // Else user is not in a room, user hasn't got a room and role
        if (roomOpt.isPresent()) {
            userModel.setRoom(roomOpt.get());

            Long roomId = roomOpt.get().getId();

            roomUser = roomUserService.getRoomUserByUserId(roomId, userId);
            roomUser.ifPresent(userModel::setRoomUser);
        }

        return userModel;
    }

    @Override
    public List<User> getUsersByRoomId(Long roomId) {
        List<User> userList = new LinkedList<>();
        List<RoomUser> roomUserList = roomUserService.getRoomUsersByRoomId(roomId);

        for (RoomUser roomUser : roomUserList) {
            String userId = roomUser.getUserId();
            getById(userId).ifPresent(userList::add);
        }

        return userList;
    }

    @Override
    public List<User> getUsersNameLike(String userNameQuery) {
        if (userNameQuery.length() > 2) {
            try {
                return userRepo.findByNameContainsIgnoreCase(userNameQuery);
            } catch (Exception e) {
                throw new DatabaseReadException(getClassOfEntity(), e, userNameQuery);
            }
        } else {
            throw new ConstraintViolationException("Search query must be greater than 3 characters.");
        }
    }

    @Override
    public boolean hasSystemRole(String userId, Role role) {
        Optional<User> userOpt = getById(userId);
        return userOpt.isPresent() && role.equals(Role.getRole(userOpt.get().getRole()));
    }

    @Override
    public boolean hasSystemPrivilege(String userId, Privilege privilege) {
        Optional<User> roomUserOpt = getById(userId);
        return roomUserOpt.isPresent() && authorityProvider.hasPrivilege(roomUserOpt.get().getRole(), privilege);
    }
}
