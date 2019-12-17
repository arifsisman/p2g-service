package vip.yazilim.p2g.web.service.p2g.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.config.security.authority.AAuthorityProvider;
import vip.yazilim.p2g.web.constant.OnlineStatus;
import vip.yazilim.p2g.web.constant.Privilege;
import vip.yazilim.p2g.web.constant.Role;
import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.entity.relation.RoomUser;
import vip.yazilim.p2g.web.exception.*;
import vip.yazilim.p2g.web.model.UserModel;
import vip.yazilim.p2g.web.repository.IUserRepo;
import vip.yazilim.p2g.web.service.p2g.IRoomService;
import vip.yazilim.p2g.web.service.p2g.IUserService;
import vip.yazilim.p2g.web.service.p2g.relation.IFriendRequestService;
import vip.yazilim.p2g.web.service.p2g.relation.IRoomUserService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyUserService;
import vip.yazilim.p2g.web.util.DBHelper;
import vip.yazilim.spring.core.exception.general.InvalidArgumentException;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;
import vip.yazilim.spring.core.service.ACrudServiceImpl;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Transactional
@Service
public class UserService extends ACrudServiceImpl<User, String> implements IUserService {

    // static fields
    private Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    // injected dependencies
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private IUserRepo userRepo;

    @Autowired
    private IRoomService roomService;

    @Autowired
    private IRoomUserService roomUserService;

    @Autowired
    private IFriendRequestService friendRequestService;

    @Autowired
    private ISpotifyUserService spotifyUserService;

    @Autowired
    private AAuthorityProvider authorityProvider;

    @Override
    protected JpaRepository<User, String> getRepository() {
        return userRepo;
    }

    @Override
    protected String getId(User entity) {
        return entity.getUuid();
    }

    @Override
    protected User preInsert(User entity) {
        entity.setUuid(DBHelper.getRandomUuid());

//        Optional<User> existingUser = getUserByEmail(entity.getEmail());
//
//        if (existingUser.isPresent()) {
//            throw new UserException("Email already exists.");
//        }

        entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        entity.setRoleName(Role.P2G_USER.getRoleName());
        return entity;
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepo.findByDisplayName(username);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    @Override
    public Optional<User> getUserByUuid(String uuid) {
        return userRepo.findByUuid(uuid);
    }

    @Override
    public Optional<UserModel> getUserModelByUserUuid(String userUuid) throws DatabaseException, RoomException, InvalidArgumentException {
        UserModel userModel = new UserModel();

        Optional<User> user;
        Optional<Room> room;
        Role role;
        List<User> friends = new ArrayList<>();
        List<User> friendRequests = new ArrayList<>();

        // Set User
        user = getById(userUuid);
        if (!user.isPresent()) {
            return Optional.empty();
        } else {
            userModel.setUser(user.get());
        }

        // Set Room & Role
        room = roomService.getRoomByUserUuid(userUuid);

        // If user joined a room, user has got a room and role
        // Else user is not in a room, user hasn't got a room and has got default role
        if (room.isPresent()) {
            userModel.setRoom(room.get());

            String roomUuid = room.get().getUuid();
            role = roomUserService.getRoleByRoomUuidAndUserUuid(roomUuid, userUuid);

            userModel.setRole(role);
        }

        // Set Friends
        try {
            friends = friendRequestService.getFriendRequestByUserUuid(userUuid);
        } catch (FriendRequestException e) {
            LOGGER.error("An error occurred while getting Friends for User[{}]", userUuid);
        }
        userModel.setFriends(friends);

        // Set Friend Requests
        try {
            friendRequests = friendRequestService.getFriendRequestsByUserUuid(userUuid);
        } catch (FriendRequestException e) {
            LOGGER.error("An error occurred while getting Friend Requests for User[{}]", userUuid);
        }
        userModel.setFriendRequests(friendRequests);

        return Optional.of(userModel);
    }

    @Override
    public List<User> getUsersByRoomUuid(String roomUuid) throws DatabaseException, InvalidArgumentException {
        List<User> userList = new LinkedList<>();
        List<RoomUser> roomUserList = roomUserService.getRoomUsersByRoomUuid(roomUuid);

        for (RoomUser roomUser : roomUserList) {
            String userUuid = roomUser.getUserUuid();
            getById(userUuid).ifPresent(userList::add);
        }

        return userList;
    }

    //todo: this method is for test purposes, delete later
    @Override
    public User createUser(String email, String username, String password) throws DatabaseException {
        User user = new User();
        user.setEmail(email);
        user.setDisplayName(username);
        user.setPassword(password);

        return create(user);
    }

    @Override
    public User setSpotifyInfo(com.wrapper.spotify.model_objects.specification.User spotifyUser, User user) throws DatabaseException, TokenException, RequestException, AccountException {

        String productType = spotifyUser.getProduct().getType();

        user.setSpotifyAccountId(spotifyUser.getId());
        user.setCountryCode(spotifyUser.getCountry().name());
        user.setOnlineStatus(OnlineStatus.ONLINE.getOnlineStatus());
        user.setSpotifyProductType(productType);

        try {
            user.setImageUrl(spotifyUser.getImages()[0].getUrl());
        } catch (RuntimeException e) {
            LOGGER.warn("Image not found for Spotify user with userId[{}]", spotifyUser.getId());
        }

        spotifyUserService.updateUsersAvailableDevices(user.getUuid());

        if (!productType.equals("premium"))
            throw new AccountException("Product type must be premium!");

        return user;
    }

    @Override
    public boolean hasSystemRole(String userUuid, Role role) throws DatabaseException, InvalidArgumentException {
        Optional<User> userOpt = getById(userUuid);
        return userOpt.isPresent() && role.equals(Role.getRole(userOpt.get().getRoleName()));
    }

    @Override
    public boolean hasSystemPrivilege(String userUuid, Privilege privilege) throws DatabaseException, InvalidArgumentException {
        Optional<User> roomUserOpt = getById(userUuid);
        return roomUserOpt.isPresent() && authorityProvider.hasPrivilege(Role.getRole(roomUserOpt.get().getRoleName()), privilege);
    }
}
