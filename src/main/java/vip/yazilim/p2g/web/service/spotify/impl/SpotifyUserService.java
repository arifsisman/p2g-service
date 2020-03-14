package vip.yazilim.p2g.web.service.spotify.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.miscellaneous.Device;
import com.wrapper.spotify.model_objects.specification.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vip.yazilim.libs.springcore.exception.general.database.DatabaseException;
import vip.yazilim.libs.springcore.exception.service.ResourceNotFoundException;
import vip.yazilim.p2g.web.entity.UserDevice;
import vip.yazilim.p2g.web.service.p2g.ISpotifyTokenService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyRequestService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyUserService;
import vip.yazilim.p2g.web.util.SpotifyHelper;

import java.io.IOException;
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

    @Autowired
    private ISpotifyTokenService tokenService;

    private Gson gson = new GsonBuilder().create();

    @Override
    public User getSpotifyUser(String userId) throws IOException, SpotifyWebApiException, DatabaseException {
        String accessToken = tokenService.getAccessTokenByUserId(userId);
        return spotifyRequest.execRequestSync((spotifyApi) -> spotifyApi.getUsersProfile(userId).build(), accessToken);
    }

    @Override
    public User getCurrentSpotifyUser(String userId) throws DatabaseException, IOException, SpotifyWebApiException {
        String accessToken = tokenService.getAccessTokenByUserId(userId);
        return spotifyRequest.execRequestSync((spotifyApi) -> spotifyApi.getCurrentUsersProfile().build(), accessToken);
    }

    @Override
    public List<UserDevice> getUsersAvailableDevices(String userId) throws DatabaseException, IOException, SpotifyWebApiException {
        List<UserDevice> userDeviceList = new LinkedList<>();
        String accessToken = tokenService.getAccessTokenByUserId(userId);
        Device[] devices = spotifyRequest.execRequestSync((spotifyApi) -> spotifyApi.getUsersAvailableDevices().build(), accessToken);

        if (devices.length == 0) {
            String err = String.format("Can not found any active Spotify device for %s, please start Spotify first.", userId);
            throw new ResourceNotFoundException(err);
        }

        for (Device d : devices) {
            userDeviceList.add(SpotifyHelper.convertDeviceToUserDevice(userId, d));
        }

        return userDeviceList;
    }

    @Override
    public boolean transferUsersPlayback(UserDevice userDevice) throws DatabaseException, IOException, SpotifyWebApiException {
        String accessToken = tokenService.getAccessTokenByUserId(userDevice.getUserId());
        JsonArray deviceJson = gson.toJsonTree(Collections.singletonList(userDevice.getId())).getAsJsonArray();
        spotifyRequest.execRequestSync((spotifyApi) -> spotifyApi.transferUsersPlayback(deviceJson).build(), accessToken);

        return true;
    }

}