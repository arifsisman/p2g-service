package vip.yazilim.p2g.web.service.spotify.impl;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.miscellaneous.Device;
import com.wrapper.spotify.model_objects.specification.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.SpotifyToken;
import vip.yazilim.p2g.web.entity.UserDevice;
import vip.yazilim.p2g.web.service.p2g.ISpotifyTokenService;
import vip.yazilim.p2g.web.service.p2g.IUserDeviceService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyRequestService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyUserService;
import vip.yazilim.spring.core.exception.general.InvalidArgumentException;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;
import vip.yazilim.spring.core.exception.web.NotFoundException;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author mustafaarifsisman - 26.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Transactional
@Service
public class SpotifyUserService implements ISpotifyUserService {

    @Autowired
    private ISpotifyRequestService spotifyRequest;

    @Autowired
    private ISpotifyTokenService tokenService;

    @Autowired
    private IUserDeviceService userDeviceService;

    @Override
    public User getSpotifyUser(String spotifyAccountId) throws IOException, SpotifyWebApiException {
        return spotifyRequest.execRequestSync((spotifyApi) -> spotifyApi.getUsersProfile(spotifyAccountId).build());
    }

    @Override
    public User getCurrentSpotifyUser(String userUuid) throws DatabaseException, IOException, SpotifyWebApiException {
        SpotifyToken spotifyToken = tokenService.getTokenByUserUuid(userUuid).orElseThrow(() -> new NotFoundException("Token not found for userUuid: " + userUuid));
        return spotifyRequest.execRequestSync((spotifyApi) -> spotifyApi.getCurrentUsersProfile().build(), spotifyToken.getAccessToken());
    }

    @Override
    public List<UserDevice> getUsersAvailableDevices(String userUuid) throws DatabaseException, IOException, SpotifyWebApiException {
        List<UserDevice> userDeviceList = new LinkedList<>();

        SpotifyToken spotifyToken = tokenService.getTokenByUserUuid(userUuid).orElseThrow(() -> new NotFoundException("Token not found for userUuid: " + userUuid));

        Device[] devices = spotifyRequest.execRequestSync((spotifyApi) -> spotifyApi.getUsersAvailableDevices().build(), spotifyToken.getAccessToken());

        if (devices.length == 0) {
            String err = String.format("Can not found any device for userUuid[%s]", userUuid);
            throw new NotFoundException(err);
        }

        for (Device d : devices) {
            UserDevice userDevice = new UserDevice();

            userDevice.setUserUuid(userUuid);
            userDevice.setDeviceId(d.getId());
            userDevice.setSource("spotify");
            userDevice.setDeviceName(d.getName());
            userDevice.setDeviceName(d.getName());
            userDevice.setActiveFlag(d.getIs_active());
            userDevice.setDeviceType(d.getType());

            userDeviceList.add(userDevice);
        }

        return userDeviceList;
    }

    @Override
    public List<UserDevice> updateUsersAvailableDevices(String userUuid) throws DatabaseException, InvalidArgumentException, IOException, SpotifyWebApiException {
        List<UserDevice> userDeviceList = getUsersAvailableDevices(userUuid);

        for (UserDevice userDevice : userDeviceList) {
            userDeviceService.create(userDevice);
        }

        return userDeviceList;
    }
}