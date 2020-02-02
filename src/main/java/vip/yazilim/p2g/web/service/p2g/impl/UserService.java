package vip.yazilim.p2g.web.service.p2g.impl;

import com.wrapper.spotify.enums.ProductType;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.config.security.authority.AAuthorityProvider;
import vip.yazilim.p2g.web.constant.enums.OnlineStatus;
import vip.yazilim.p2g.web.constant.enums.Privilege;
import vip.yazilim.p2g.web.constant.enums.Role;
import vip.yazilim.p2g.web.entity.*;
import vip.yazilim.p2g.web.exception.AccountException;
import vip.yazilim.p2g.web.exception.SpotifyAccountException;
import vip.yazilim.p2g.web.model.InviteModel;
import vip.yazilim.p2g.web.model.RoomModel;
import vip.yazilim.p2g.web.model.UserModel;
import vip.yazilim.p2g.web.repository.IUserRepo;
import vip.yazilim.p2g.web.service.p2g.*;
import vip.yazilim.p2g.web.service.spotify.ISpotifyUserService;
import vip.yazilim.p2g.web.util.TimeHelper;
import vip.yazilim.spring.core.exception.general.InvalidArgumentException;
import vip.yazilim.spring.core.exception.general.InvalidUpdateException;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;
import vip.yazilim.spring.core.exception.web.NotFoundException;
import vip.yazilim.spring.core.service.ACrudServiceImpl;

import javax.transaction.Transactional;
import java.io.IOException;
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
    private IRoomInviteService roomInviteService;

    @Autowired
    private ISpotifyUserService spotifyUserService;

    @Autowired
    private IUserDeviceService userDeviceService;

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
    protected User preInsert(User entity) {
        Optional<User> existingUser = getUserById(entity.getId());
        if (existingUser.isPresent()) {
            String err = String.format("Account with ID [%s] already exists.", existingUser.get().getId());
            throw new AccountException(err);
        }

        entity.setCreationDate(TimeHelper.getLocalDateTimeNow());
        entity.setRole(Role.P2G_USER.getRole());
        entity.setOnlineStatus(OnlineStatus.ONLINE.getOnlineStatus());
        entity.setShowActivityFlag(true);
        entity.setShowFriendsFlag(true);
        return entity;
    }

    @Override
    public Optional<User> getUserById(String id) {
        return userRepo.findById(id);
    }

    @Override
    public UserModel getUserModelByUserId(String userId) throws DatabaseException, InvalidArgumentException {
        UserModel userModel = new UserModel();

        Optional<User> userOpt;
        Optional<Room> roomOpt;
        List<UserDevice> userDevices;
        Optional<RoomUser> roomUser;
        List<User> friends;
        List<User> friendRequests;

        // Set User
        userOpt = getById(userId);
        if (!userOpt.isPresent()) {
            throw new NotFoundException("User not found");
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

            roomUser = roomUserService.getRoomUser(roomId, userId);
            roomUser.ifPresent(userModel::setRoomUser);
        }

        // Set User Devices
        userDevices = userDeviceService.getUserDevicesByUserId(userId);
        userModel.setUserDevices(userDevices);

        // Set Friends
        friends = friendRequestService.getFriendsByUserId(userId);
        userModel.setFriends(friends);

        // Set Friend Requests
        friendRequests = friendRequestService.getFriendRequestsUsersByUserId(userId);
        userModel.setFriendRequests(friendRequests);

        return userModel;
    }

    @Override
    public InviteModel getInviteModelByUserId(String userId) throws DatabaseException, InvalidArgumentException {
        InviteModel inviteModel = new InviteModel();

        List<FriendRequest> friendRequests;
        List<User> friendRequestUsers;
        List<RoomInvite> roomInvites;
        List<RoomModel> roomModels = new LinkedList<>();

        friendRequests = friendRequestService.getFriendRequestsByUserId(userId);
        inviteModel.setFriendRequests(friendRequests);

        friendRequestUsers = friendRequestService.getFriendRequestsUsersByUserId(userId);
        inviteModel.setFriendRequestUsers(friendRequestUsers);

        roomInvites = roomInviteService.getRoomInvitesByUserId(userId);
        inviteModel.setRoomInvites(roomInvites);

        for(RoomInvite ri : roomInvites){
            roomModels.add(roomService.getRoomModelByRoomId(ri.getRoomId()));
        }
        inviteModel.setRoomModels(roomModels);

        return inviteModel;
    }

    @Override
    public List<User> getUsersByRoomId(Long roomId) throws DatabaseException, InvalidArgumentException {
        List<User> userList = new LinkedList<>();
        List<RoomUser> roomUserList = roomUserService.getRoomUsersByRoomId(roomId);

        for (RoomUser roomUser : roomUserList) {
            String userId = roomUser.getUserId();
            getById(userId).ifPresent(userList::add);
        }

        return userList;
    }

    @Override
    public User createUser(String id, String email, String username, String password) throws DatabaseException, InvalidArgumentException {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setName(username);

        return create(user);
    }

    @Override
    public User setSpotifyInfo(com.wrapper.spotify.model_objects.specification.User spotifyUser, User user) throws DatabaseException, InvalidArgumentException, IOException, SpotifyWebApiException, InvalidUpdateException {
        String productType = spotifyUser.getProduct().getType();

        if (!productType.equals(ProductType.PREMIUM.getType())) {
            throw new SpotifyAccountException("Spotify account must be premium");
        }

        user.setName(spotifyUser.getDisplayName());
        user.setEmail(spotifyUser.getEmail());
        user.setCountryCode(spotifyUser.getCountry().name());
        user.setSpotifyProductType(productType);

        Image[] images = spotifyUser.getImages();
        if (images.length > 0) {
            user.setImageUrl(images[0].getUrl());
        }

        spotifyUserService.updateUsersAvailableDevices(user.getId());
        return update(user);
    }

    @Override
    public boolean hasSystemRole(String userId, Role role) throws DatabaseException, InvalidArgumentException {
        Optional<User> userOpt = getById(userId);
        return userOpt.isPresent() && role.equals(Role.getRole(userOpt.get().getRole()));
    }

    @Override
    public boolean hasSystemPrivilege(String userId, Privilege privilege) throws DatabaseException, InvalidArgumentException {
        Optional<User> roomUserOpt = getById(userId);
        return roomUserOpt.isPresent() && authorityProvider.hasPrivilege(Role.getRole(roomUserOpt.get().getRole()), privilege);
    }
}
