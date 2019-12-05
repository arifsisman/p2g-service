package vip.yazilim.p2g.web.service.spotify.impl;

import com.wrapper.spotify.model_objects.miscellaneous.Device;
import com.wrapper.spotify.model_objects.specification.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.SpotifyToken;
import vip.yazilim.p2g.web.entity.relation.UserDevice;
import vip.yazilim.p2g.web.exception.AccountException;
import vip.yazilim.p2g.web.exception.RequestException;
import vip.yazilim.p2g.web.exception.TokenException;
import vip.yazilim.p2g.web.service.p2g.ITokenService;
import vip.yazilim.p2g.web.service.p2g.relation.IUserDeviceService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyRequestService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyUserService;
import vip.yazilim.spring.core.exception.database.DatabaseException;

import javax.transaction.Transactional;
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
    private ITokenService tokenService;

    @Autowired
    private IUserDeviceService userDeviceService;

    @Override
    public User getSpotifyUser(String spotifyAccountId) throws RequestException {
        return spotifyRequest.execRequestSync((spotifyApi) -> spotifyApi.getUsersProfile(spotifyAccountId).build());
    }

    @Override
    public User getCurrentSpotifyUser(String userUuid) throws DatabaseException, TokenException, RequestException {
        SpotifyToken spotifyToken = tokenService.getTokenByUserUuid(userUuid).orElseThrow(() -> new TokenException("Token not found!"));
        return spotifyRequest.execRequestSync((spotifyApi) -> spotifyApi.getCurrentUsersProfile().build(), spotifyToken.getAccessToken());
    }

    @Override
    public List<UserDevice> getUsersAvailableDevices(String userUuid) throws DatabaseException, TokenException, RequestException, AccountException {
        List<UserDevice> userDeviceList = new LinkedList<>();

        SpotifyToken spotifyToken = tokenService.getTokenByUserUuid(userUuid).orElseThrow(() -> new TokenException("Token not found"));

        Device[] devices = spotifyRequest.execRequestSync((spotifyApi) -> spotifyApi.getUsersAvailableDevices().build(), spotifyToken.getAccessToken());

        for (Device d : devices) {
            UserDevice userDevice = new UserDevice();

            userDevice.setUserUuid(userUuid);
            userDevice.setDeviceId(d.getId());
            userDevice.setDeviceName(d.getName());
            userDevice.setDeviceName(d.getName());
            userDevice.setActiveFlag(d.getIs_active());
            userDevice.setDeviceType(d.getType());

            userDeviceList.add(userDevice);
        }

        if (userDeviceList.isEmpty()) {
            String err = String.format("Can not found any device for userUuid[%s]", userUuid);
            throw new AccountException(err);
        }

        return userDeviceList;
    }

    @Override
    public List<UserDevice> updateUsersAvailableDevices(String userUuid) throws DatabaseException, TokenException, RequestException, AccountException {
        List<UserDevice> userDeviceList = getUsersAvailableDevices(userUuid);

        for (UserDevice userDevice : userDeviceList) {
            userDeviceService.create(userDevice);
        }

        return userDeviceList;
    }
}