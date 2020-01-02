package vip.yazilim.p2g.web.service.p2g.impl;

import com.wrapper.spotify.enums.ProductType;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.config.security.authority.AAuthorityProvider;
import vip.yazilim.p2g.web.constant.OnlineStatus;
import vip.yazilim.p2g.web.constant.Privilege;
import vip.yazilim.p2g.web.constant.Role;
import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.entity.RoomUser;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.exception.AccountException;
import vip.yazilim.p2g.web.exception.SpotifyAccountException;
import vip.yazilim.p2g.web.model.UserModel;
import vip.yazilim.p2g.web.repository.IUserRepo;
import vip.yazilim.p2g.web.service.p2g.IFriendRequestService;
import vip.yazilim.p2g.web.service.p2g.IRoomService;
import vip.yazilim.p2g.web.service.p2g.IRoomUserService;
import vip.yazilim.p2g.web.service.p2g.IUserService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyUserService;
import vip.yazilim.p2g.web.util.TimeHelper;
import vip.yazilim.spring.core.exception.general.InvalidArgumentException;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;
import vip.yazilim.spring.core.exception.web.NotFoundException;
import vip.yazilim.spring.core.service.ACrudServiceImpl;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Transactional
@Service
public class UserService extends ACrudServiceImpl<User, UUID> implements IUserService {

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
    protected JpaRepository<User, UUID> getRepository() {
        return userRepo;
    }

    @Override
    protected UUID getId(User entity) {
        return entity.getUuid();
    }

    @Override
    protected User preInsert(User entity) {
        Optional<User> existingUser = getUserByEmail(entity.getEmail());
        if (existingUser.isPresent()) {
            throw new AccountException("Email already exists.");
        }

        entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        entity.setCreationDate(TimeHelper.getLocalDateTimeNow());
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
    public UserModel getUserModelByUserUuid(UUID userUuid) throws DatabaseException, InvalidArgumentException {
        UserModel userModel = new UserModel();

        Optional<User> userOpt;
        Optional<Room> roomOpt;
        Role roomRole;
        List<User> friends;
        List<User> friendRequests;

        // Set User
        userOpt = getById(userUuid);
        if (!userOpt.isPresent()) {
            throw new NotFoundException("User not found");
        }

        // Set Room & Role
        roomOpt = roomService.getRoomByUserUuid(userUuid);

        // If user joined a room, user has got a room and role
        // Else user is not in a room, user hasn't got a room and role
        if (roomOpt.isPresent()) {
            userModel.setRoom(roomOpt.get());

            Long roomId = roomOpt.get().getId();
            roomRole = roomUserService.getRoleByRoomIdAndUserUuid(roomId, userUuid);
            userModel.setRoomRole(roomRole);
        }

        // Set Friends
        friends = friendRequestService.getFriendsByUserUuid(userUuid);
        userModel.setFriends(friends);

        // Set Friend Requests
        friendRequests = friendRequestService.getFriendRequestsByUserUuid(userUuid);
        userModel.setFriendRequests(friendRequests);

        return userModel;
    }

    @Override
    public List<User> getUsersByroomId(Long roomId) throws DatabaseException, InvalidArgumentException {
        List<User> userList = new LinkedList<>();
        List<RoomUser> roomUserList = roomUserService.getRoomUsersByRoomId(roomId);

        for (RoomUser roomUser : roomUserList) {
            UUID userUuid = roomUser.getUserUuid();
            getById(userUuid).ifPresent(userList::add);
        }

        return userList;
    }

    //todo: this method is for test purposes, delete later
    @Override
    public User createUser(String email, String username, String password) throws DatabaseException, InvalidArgumentException {
        User user = new User();
        user.setEmail(email);
        user.setDisplayName(username);
        user.setPassword(password);

        return create(user);
    }

    @Override
    public User setSpotifyInfo(com.wrapper.spotify.model_objects.specification.User spotifyUser, User user) throws DatabaseException, InvalidArgumentException, IOException, SpotifyWebApiException {

        String productType = spotifyUser.getProduct().getType();

        if (!productType.equals(ProductType.PREMIUM.getType())) {
            throw new SpotifyAccountException("Product type must be premium");
        }

        user.setSpotifyAccountId(spotifyUser.getId());
        user.setCountryCode(spotifyUser.getCountry().name());
        user.setOnlineStatus(OnlineStatus.ONLINE.getOnlineStatus());
        user.setSpotifyProductType(productType);

        Image[] images = spotifyUser.getImages();
        if (images.length > 0) {
            user.setImageUrl(images[0].getUrl());
        }

        spotifyUserService.updateUsersAvailableDevices(user.getUuid());

        return user;
    }

    @Override
    public boolean hasSystemRole(UUID userUuid, Role role) throws DatabaseException, InvalidArgumentException {
        Optional<User> userOpt = getById(userUuid);
        return userOpt.isPresent() && role.equals(Role.getRole(userOpt.get().getRoleName()));
    }

    @Override
    public boolean hasSystemPrivilege(UUID userUuid, Privilege privilege) throws DatabaseException, InvalidArgumentException {
        Optional<User> roomUserOpt = getById(userUuid);
        return roomUserOpt.isPresent() && authorityProvider.hasPrivilege(Role.getRole(roomUserOpt.get().getRoleName()), privilege);
    }
}
