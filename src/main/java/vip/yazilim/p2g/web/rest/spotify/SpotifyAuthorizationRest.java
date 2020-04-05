package vip.yazilim.p2g.web.rest.spotify;

import com.wrapper.spotify.enums.ProductType;
import com.wrapper.spotify.model_objects.specification.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vip.yazilim.libs.springcore.rest.model.RestResponse;
import vip.yazilim.p2g.web.config.annotation.HasSystemRole;
import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.entity.RoomUser;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.entity.UserDevice;
import vip.yazilim.p2g.web.enums.OnlineStatus;
import vip.yazilim.p2g.web.enums.Role;
import vip.yazilim.p2g.web.exception.SpotifyException;
import vip.yazilim.p2g.web.service.p2g.*;
import vip.yazilim.p2g.web.service.spotify.ISpotifyUserService;
import vip.yazilim.p2g.web.util.SecurityHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

import static vip.yazilim.p2g.web.constant.Constants.API_SPOTIFY;

/**
 * @author mustafaarifsisman - 23.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@RestController
@RequestMapping(API_SPOTIFY)
public class SpotifyAuthorizationRest {

    private Logger LOGGER = LoggerFactory.getLogger(SpotifyAuthorizationRest.class);

    @Autowired
    private ISpotifyTokenService tokenService;

    @Autowired
    private IUserService userService;

    @Autowired
    private ISpotifyUserService spotifyUserService;

    @Autowired
    private IRoomUserService roomUserService;

    @Autowired
    private IRoomService roomService;

    @Autowired
    private IUserDeviceService userDeviceService;

    @GetMapping("/login")
    public RestResponse<User> login(HttpServletRequest request, HttpServletResponse response) {
        String userId = SecurityHelper.getUserId();
        Optional<User> userOpt = userService.getById(userId);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            LOGGER.info("[{}] :: Logged in", userId);
            updateUserAccessToken();

            return RestResponse.generateResponse(updateUserSpotifyInfos(user), HttpStatus.OK, request, response);
        } else {
            return register(request, response);
        }
    }

    @HasSystemRole(role = Role.P2G_USER)
    @PostMapping("/logout")
    public RestResponse<Boolean> logout(HttpServletRequest request, HttpServletResponse response) {
        String userId = SecurityHelper.getUserId();
        Optional<RoomUser> roomUserOpt = roomUserService.getRoomUserByUserId(userId);

        Optional<User> userOpt = userService.getById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setOnlineStatus(OnlineStatus.OFFLINE.getOnlineStatus());
            userService.update(user);
        }

        LOGGER.info("[{}] :: Logged out", userId);

        if (roomUserOpt.isPresent()) {
            RoomUser roomUser = roomUserOpt.get();
            Optional<Room> roomOpt = roomService.getRoomByUserId(userId);
            if (roomOpt.isPresent()) {
                Room room = roomOpt.get();
                if (room.getOwnerId().equals(userId)) {
                    return RestResponse.generateResponse(roomService.delete(room), HttpStatus.OK, request, response);
                } else {
                    return RestResponse.generateResponse(roomUserService.delete(roomUser), HttpStatus.OK, request, response);
                }
            }
        }

        return RestResponse.generateResponse(true, HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @PutMapping("/token")
    public RestResponse<String> updateUserAccessToken(HttpServletRequest request, HttpServletResponse response, @RequestBody String accessToken) {
        return RestResponse.generateResponse(tokenService.saveUserToken(SecurityHelper.getUserId(), accessToken), HttpStatus.OK, request, response);
    }

    private RestResponse<User> register(HttpServletRequest request, HttpServletResponse response) {
        String userId = SecurityHelper.getUserId();
        String email = SecurityHelper.getUserEmail();
        String userName = SecurityHelper.getUserDisplayName();

        User user = userService.createUser(userId, email, userName);

        LOGGER.info("[{}] :: Registered and logged in", userId);
        updateUserAccessToken();

        return RestResponse.generateResponse(updateUserSpotifyInfos(user), HttpStatus.OK, request, response);
    }

    private User updateUserSpotifyInfos(User user) {
        String userId = user.getId();

        com.wrapper.spotify.model_objects.specification.User spotifyUser = spotifyUserService.getCurrentSpotifyUser(user.getId());

        String productType = spotifyUser.getProduct().getType();
        if (!productType.equals(ProductType.PREMIUM.getType())) {
            throw new SpotifyException("Spotify account must be premium");
        }

        user.setName(spotifyUser.getDisplayName());
        user.setEmail(spotifyUser.getEmail());
        user.setCountryCode(spotifyUser.getCountry().name());
        user.setOnlineStatus(OnlineStatus.ONLINE.getOnlineStatus());

        Image[] images = spotifyUser.getImages();
        if (images.length > 0) {
            user.setImageUrl(images[0].getUrl());
        }

        Optional<UserDevice> oldUserDeviceOpt = userDeviceService.getUsersActiveDevice(userId);
        oldUserDeviceOpt.ifPresent(userDevice -> userDeviceService.delete(userDevice));

        List<UserDevice> userDeviceList = spotifyUserService.getUsersAvailableDevices(userId);
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

        return userService.update(user);
    }

    public String updateUserAccessToken() {
        return tokenService.saveUserToken(SecurityHelper.getUserId(), SecurityHelper.getUserAccessToken());
    }

}