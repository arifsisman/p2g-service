package vip.yazilim.p2g.web.service.p2g.impl;

import com.wrapper.spotify.enums.ProductType;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.config.security.authority.AAuthorityProvider;
import vip.yazilim.p2g.web.constant.enums.OnlineStatus;
import vip.yazilim.p2g.web.constant.enums.Privilege;
import vip.yazilim.p2g.web.constant.enums.Role;
import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.entity.RoomUser;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.entity.UserDevice;
import vip.yazilim.p2g.web.exception.SpotifyAccountException;
import vip.yazilim.p2g.web.model.UserModel;
import vip.yazilim.p2g.web.repository.IUserRepo;
import vip.yazilim.p2g.web.service.p2g.*;
import vip.yazilim.p2g.web.service.spotify.ISpotifyUserService;
import vip.yazilim.p2g.web.util.TimeHelper;
import vip.yazilim.spring.core.exception.GeneralException;
import vip.yazilim.spring.core.exception.InvalidArgumentException;
import vip.yazilim.spring.core.exception.database.DatabaseCreateException;
import vip.yazilim.spring.core.exception.database.DatabaseException;
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
    public Optional<User> getUserById(String id) {
        return userRepo.findById(id);
    }

    @Override
    public UserModel getUserModelByUserId(String userId) throws DatabaseException, InvalidArgumentException {
        UserModel userModel = new UserModel();

        Optional<User> userOpt;
        Optional<Room> roomOpt;
        Optional<RoomUser> roomUser;

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

        return userModel;
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
    public User createUser(String id, String email, String username) throws GeneralException {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setName(username);

        return create(user);
    }

    @Override
    public User setSpotifyInfo(com.wrapper.spotify.model_objects.specification.User spotifyUser, User user) throws GeneralException, IOException, SpotifyWebApiException {
        String userId = user.getId();
        String productType = spotifyUser.getProduct().getType();

        if (!productType.equals(ProductType.PREMIUM.getType())) {
            throw new SpotifyAccountException("Spotify account must be premium");
        }

        user.setName(spotifyUser.getDisplayName());
        user.setEmail(spotifyUser.getEmail());
        user.setCountryCode(spotifyUser.getCountry().name());

        Image[] images = spotifyUser.getImages();
        if (images.length > 0) {
            user.setImageUrl(images[0].getUrl());
        }

        Optional<UserDevice> oldUserDeviceOpt = userDeviceService.getUsersActiveDevice(userId);
        if (oldUserDeviceOpt.isPresent()) {
            userDeviceService.delete(oldUserDeviceOpt.get());
        }

        List<UserDevice> userDeviceList = spotifyUserService.getUsersAvailableDevices(userId);

        if (userDeviceList.isEmpty()) {
            String err = String.format("Can not found any device logged into Spotify App for userId[%s]. Please restart Spotify App first!", userId);
            throw new NotFoundException(err);
        }

        try {
            boolean userDeviceCreated = false;

            for (UserDevice userDevice : userDeviceList) {
                if (userDevice.getActiveFlag()) {
                    userDeviceService.create(userDevice);
                    userDeviceCreated = true;
                }
            }

            if (!userDeviceCreated) {
                UserDevice userDevice = userDeviceList.get(0);
                userDevice.setActiveFlag(true);
                userDeviceService.create(userDevice);
            }
        } catch (Exception exception) {
            throw new DatabaseCreateException(getClass(), userId, exception);
        }

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
