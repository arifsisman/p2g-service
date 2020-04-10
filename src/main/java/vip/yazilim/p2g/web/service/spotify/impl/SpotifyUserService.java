package vip.yazilim.p2g.web.service.spotify.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.wrapper.spotify.model_objects.miscellaneous.Device;
import com.wrapper.spotify.model_objects.specification.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.UserDevice;
import vip.yazilim.p2g.web.exception.SpotifyException;
import vip.yazilim.p2g.web.service.spotify.ISpotifyRequestService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyUserService;
import vip.yazilim.p2g.web.util.SecurityHelper;
import vip.yazilim.p2g.web.util.SpotifyHelper;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author mustafaarifsisman - 26.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class SpotifyUserService implements ISpotifyUserService {

    @Autowired
    private ISpotifyRequestService spotifyRequest;

    private Gson gson = new GsonBuilder().create();

    @Override
    public User getSpotifyUser(String userId) {
        return spotifyRequest.execRequestSync((spotifyApi) -> spotifyApi.getUsersProfile(userId).build(), SecurityHelper.getUserAccessToken());
    }

    @Override
    public User getCurrentSpotifyUser() {
        return spotifyRequest.execRequestSync((spotifyApi) -> spotifyApi.getCurrentUsersProfile().build(), SecurityHelper.getUserAccessToken());
    }

    @Override
    public List<UserDevice> getUsersAvailableDevices(String userId) {
        List<UserDevice> userDeviceList = new LinkedList<>();
        Device[] devices = spotifyRequest.execRequestSync((spotifyApi) -> spotifyApi.getUsersAvailableDevices().build(), SecurityHelper.getUserAccessToken());

        if (devices.length == 0) {
            throw new SpotifyException("Can not found any active Spotify device, please start Spotify first.");
        }

        for (Device d : devices) {
            userDeviceList.add(SpotifyHelper.convertDeviceToUserDevice(userId, d));
        }

        return userDeviceList;
    }

    @Override
    public boolean transferUsersPlayback(UserDevice userDevice) {
        JsonArray deviceJson = gson.toJsonTree(Collections.singletonList(userDevice.getId())).getAsJsonArray();
        spotifyRequest.execRequestSync((spotifyApi) -> spotifyApi.transferUsersPlayback(deviceJson).build(), SecurityHelper.getUserAccessToken());

        return true;
    }

}