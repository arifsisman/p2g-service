package vip.yazilim.p2g.web.service.p2g.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.constant.OnlineStatus;
import vip.yazilim.p2g.web.entity.Role;
import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.entity.relation.RoomUser;
import vip.yazilim.p2g.web.exception.*;
import vip.yazilim.p2g.web.model.UserModel;
import vip.yazilim.p2g.web.repository.IUserRepo;
import vip.yazilim.p2g.web.service.p2g.IRoleService;
import vip.yazilim.p2g.web.service.p2g.IRoomService;
import vip.yazilim.p2g.web.service.p2g.IUserFriendsService;
import vip.yazilim.p2g.web.service.p2g.IUserService;
import vip.yazilim.p2g.web.service.p2g.relation.IRoomUserService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyUserService;
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
public class UserService extends ACrudServiceImpl<User, String> implements IUserService {

    // static fields
    private Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    // injected dependencies
    @Autowired
    private IUserRepo userRepo;

    @Autowired
    private IRoleService roleService;

    @Autowired
    private IRoomService roomService;

    @Autowired
    private IRoomUserService roomUserService;

    @Autowired
    private IUserFriendsService userFriendsService;

    @Autowired
    private ISpotifyUserService spotifyUserService;

    @Override
    protected JpaRepository<User, String> getRepository() {
        return userRepo;
    }

    @Override
    protected String getId(User entity) {
        return entity.getUuid();
    }

    //TODO: uncomment
//    @Override
//    protected User preInsert(User entity) {
//        entity.setUuid(DBHelper.getRandomUuid());
//        return entity;
//    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    @Override
    public Optional<User> getUserByUuid(String uuid) {
        return userRepo.findByUuid(uuid);
    }

    @Override
    public Optional<UserModel> getUserModelByUserUuid(String userUuid) throws DatabaseException, RoleException {
        UserModel userModel = new UserModel();

        Optional<User> user;
        Optional<Room> room;
        Optional<Role> role;
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
            role = roleService.getRoleByRoomAndUser(roomUuid, userUuid);

            role.ifPresent(userModel::setRole);
        } else {
            Role defaultRole = roleService.getDefaultRole();
            userModel.setRole(defaultRole);
        }

        // Set Friends
        try {
            friends = userFriendsService.getUserFriendsByUserUuid(userUuid);
        } catch (UserFriendsException e) {
            LOGGER.error("An error occurred while getting Friends for User[{}]", userUuid);
        }
        userModel.setFriends(friends);

        // Set Friend Requests
        try {
            friendRequests = userFriendsService.getUserFriendRequestsByUserUuid(userUuid);
        } catch (UserFriendsException e) {
            LOGGER.error("An error occurred while getting Friend Requests for User[{}]", userUuid);
        }
        userModel.setFriendRequests(friendRequests);

        return Optional.of(userModel);
    }

    @Override
    public List<User> getUsersByRoomUuid(String roomUuid) throws DatabaseException {

        List<User> userList = new ArrayList<>();
        List<RoomUser> roomUserList = roomUserService.getRoomUsersByRoomUuid(roomUuid);

        for (RoomUser roomUser : roomUserList) {
            String userUuid = roomUser.getUserUuid();
            getById(userUuid).ifPresent(userList::add);
        }

        return userList;
    }

    @Override
    public User setSpotifyInfo(com.wrapper.spotify.model_objects.specification.User spotifyUser, User user) throws DatabaseException, TokenException, RequestException, AccountException {

        String productType = spotifyUser.getProduct().getType();

        user.setSpotifyAccountId(spotifyUser.getId());
        user.setCountryCode(spotifyUser.getCountry().getName());
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

}
