package vip.yazilim.p2g.web.service.spotify.impl;

import com.google.gson.JsonArray;
import com.wrapper.spotify.enums.ProductType;
import com.wrapper.spotify.model_objects.miscellaneous.Device;
import com.wrapper.spotify.model_objects.specification.Image;
import com.wrapper.spotify.model_objects.specification.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.config.GsonConfig;
import vip.yazilim.p2g.web.config.OnlineStatusScheduler;
import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.entity.RoomUser;
import vip.yazilim.p2g.web.entity.UserDevice;
import vip.yazilim.p2g.web.enums.OnlineStatus;
import vip.yazilim.p2g.web.enums.Role;
import vip.yazilim.p2g.web.exception.SpotifyException;
import vip.yazilim.p2g.web.service.p2g.*;
import vip.yazilim.p2g.web.service.spotify.ISpotifyRequestService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyUserService;
import vip.yazilim.p2g.web.util.SecurityHelper;
import vip.yazilim.p2g.web.util.TimeHelper;

import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 26.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Slf4j
@Service
public class SpotifyUserService implements ISpotifyUserService {

    private final OnlineStatusScheduler onlineStatusScheduler;
    private final IUserService userService;
    private final IUserDeviceService userDeviceService;
    private final ISpotifyRequestService spotifyRequest;
    private final IRoomUserService roomUserService;
    private final IRoomService roomService;
    private final ISpotifyTokenService tokenService;

    private final GsonConfig gsonConfig;

    public SpotifyUserService(OnlineStatusScheduler onlineStatusScheduler,
                              @Lazy IUserService userService,
                              @Lazy IUserDeviceService userDeviceService,
                              ISpotifyRequestService spotifyRequest,
                              IRoomUserService roomUserService,
                              @Lazy IRoomService roomService,
                              @Lazy ISpotifyTokenService tokenService,
                              GsonConfig gsonConfig) {
        this.onlineStatusScheduler = onlineStatusScheduler;
        this.userService = userService;
        this.userDeviceService = userDeviceService;
        this.spotifyRequest = spotifyRequest;
        this.roomUserService = roomUserService;
        this.roomService = roomService;
        this.tokenService = tokenService;
        this.gsonConfig = gsonConfig;
    }

    @Override
    public User getSpotifyUser(String userId) {
        return spotifyRequest.execRequestAsync(spotifyApi -> spotifyApi.getUsersProfile(userId).build(), SecurityHelper.getUserAccessToken());
    }

    @Override
    public User getCurrentSpotifyUser() {
        return spotifyRequest.execRequestAsync(spotifyApi -> spotifyApi.getCurrentUsersProfile().build(), SecurityHelper.getUserAccessToken());
    }

    @Override
    public List<UserDevice> getUsersAvailableDevices(String userId) {
        List<UserDevice> userDeviceList = new LinkedList<>();
        Device[] devices = spotifyRequest.execRequestAsync(spotifyApi -> spotifyApi.getUsersAvailableDevices().build(), SecurityHelper.getUserAccessToken());

        if (devices.length == 0) {
            throw new SpotifyException("Can not found any active Spotify device, please start Spotify first.");
        }

        for (Device d : devices) {
            userDeviceList.add(convertDeviceToUserDevice(userId, d));
        }

        return userDeviceList;
    }

    @Override
    public void transferUsersPlayback(UserDevice userDevice) {
        JsonArray deviceJson = gsonConfig.getGson().toJsonTree(Collections.singletonList(userDevice.getId())).getAsJsonArray();
        spotifyRequest.execRequestAsync(spotifyApi -> spotifyApi.transferUsersPlayback(deviceJson).build(), SecurityHelper.getUserAccessToken());
    }

    @Override
    public vip.yazilim.p2g.web.entity.User login() {
        String userId = SecurityHelper.getUserId();
        com.wrapper.spotify.model_objects.specification.User spotifyUser = getCurrentSpotifyUser();

        Optional<vip.yazilim.p2g.web.entity.User> userOpt = userService.getById(userId);
        if (userOpt.isPresent()) {
            return saveUser(spotifyUser, userOpt.get());
        } else {
            vip.yazilim.p2g.web.entity.User newUser = new vip.yazilim.p2g.web.entity.User();
            newUser.setCreationDate(TimeHelper.getLocalDateTimeNow());
            newUser.setSystemRole(Role.P2G_USER.getRole());
            return saveUser(spotifyUser, newUser);
        }
    }

    @Override
    public boolean logout() {
        String userId = SecurityHelper.getUserId();
        Optional<RoomUser> roomUserOpt = roomUserService.getRoomUserByUserId(userId);

        Optional<vip.yazilim.p2g.web.entity.User> userOpt = userService.getById(userId);
        if (userOpt.isPresent()) {
            vip.yazilim.p2g.web.entity.User user = userOpt.get();
            user.setOnlineStatus(OnlineStatus.OFFLINE.getOnlineStatus());
            userService.update(user);
        }

        log.info("[{}] :: Logged out", userId);

        if (roomUserOpt.isPresent()) {
            RoomUser roomUser = roomUserOpt.get();
            Optional<Room> roomOpt = roomService.getRoomByUserId(userId);
            if (roomOpt.isPresent()) {
                Room room = roomOpt.get();
                if (room.getOwnerId().equals(userId)) {
                    return roomService.delete(room);
                } else {
                    return roomUserService.delete(roomUser);
                }
            }
        }

        return true;
    }

    @Override
    public vip.yazilim.p2g.web.entity.User saveUser(com.wrapper.spotify.model_objects.specification.User spotifyUser, vip.yazilim.p2g.web.entity.User user) {
        if (!isAccountPremium(spotifyUser)) {
            throw new SpotifyException("Spotify account must be premium");
        }

        String userId = SecurityHelper.getUserId();

        user.setId(userId);
        user.setName(spotifyUser.getDisplayName());
        user.setEmail(spotifyUser.getEmail());
        user.setCountryCode(spotifyUser.getCountry().name());
        user.setOnlineStatus(OnlineStatus.ONLINE.getOnlineStatus());
        user.setLastLogin(TimeHelper.getLocalDateTimeNow());

        Image[] images = spotifyUser.getImages();
        if (images.length > 0) {
            user.setImageUrl(images[0].getUrl());
        }

        // delete old or inactive device if present
        userDeviceService.getUsersActiveDevice(userId).ifPresent(userDeviceService::delete);

        List<UserDevice> userDeviceList = getUsersAvailableDevices(userId);
        boolean userDeviceCreated = false;

        for (UserDevice userDevice : userDeviceList) {
            if (userDevice.getActiveFlag() != null && userDevice.getActiveFlag()) {
                userDeviceService.create(userDevice);
                userDeviceCreated = true;
            }
        }

        if (!userDeviceCreated) {
            UserDevice userDevice = userDeviceList.get(0);
            userDevice.setActiveFlag(true);
            userDeviceService.create(userDevice);
        }

        tokenService.saveUserToken(userId, SecurityHelper.getUserAccessToken());
        vip.yazilim.p2g.web.entity.User savedUser = userService.save(user);

        goOfflineAfterOneHour(userId);
        log.info("[{}] :: Logged in", userId);

        return savedUser;
    }

    private UserDevice convertDeviceToUserDevice(String userId, Device device) {
        UserDevice userDevice = new UserDevice();

        userDevice.setUserId(userId);
        userDevice.setId(device.getId());
        userDevice.setDeviceName(device.getName());
        userDevice.setDeviceType(device.getType());
        userDevice.setActiveFlag(device.getIs_active());

        return userDevice;
    }

    private boolean isAccountPremium(com.wrapper.spotify.model_objects.specification.User spotifyUser) {
        return spotifyUser.getProduct().getType().equals(ProductType.PREMIUM.getType());
    }

    private void goOfflineAfterOneHour(String userId) {
        int delayMs = 59 * 60 * 1000;

        onlineStatusScheduler.getScheduler()
                .scheduleWithFixedDelay(() -> userService.getById(userId).ifPresent(user -> {
                    if (ChronoUnit.MILLIS.between(user.getLastLogin(), TimeHelper.getLocalDateTimeNow()) >= delayMs) {
                        user.setOnlineStatus(OnlineStatus.OFFLINE.getOnlineStatus());
                        userService.update(user);
                        log.info("[{}] :: Logged out :: SYSTEM)", userId);
                    }
                }), TimeHelper.getDatePostponed(delayMs), Long.MAX_VALUE);
    }
}