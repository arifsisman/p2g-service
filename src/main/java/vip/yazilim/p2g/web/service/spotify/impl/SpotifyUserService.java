package vip.yazilim.p2g.web.service.spotify.impl;

import com.google.gson.JsonArray;
import com.wrapper.spotify.model_objects.miscellaneous.Device;
import com.wrapper.spotify.model_objects.specification.User;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.config.GsonConfig;
import vip.yazilim.p2g.web.entity.UserDevice;
import vip.yazilim.p2g.web.exception.SpotifyException;
import vip.yazilim.p2g.web.service.spotify.ISpotifyRequestService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyUserService;
import vip.yazilim.p2g.web.util.SecurityHelper;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author mustafaarifsisman - 26.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class SpotifyUserService implements ISpotifyUserService {

    private final ISpotifyRequestService spotifyRequest;
    private final GsonConfig gsonConfig;

    public SpotifyUserService(ISpotifyRequestService spotifyRequest, GsonConfig gsonConfig) {
        this.spotifyRequest = spotifyRequest;
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
    public boolean transferUsersPlayback(UserDevice userDevice) {
        JsonArray deviceJson = gsonConfig.getGson().toJsonTree(Collections.singletonList(userDevice.getId())).getAsJsonArray();
        spotifyRequest.execRequestAsync(spotifyApi -> spotifyApi.transferUsersPlayback(deviceJson).build(), SecurityHelper.getUserAccessToken());

        return true;
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

}