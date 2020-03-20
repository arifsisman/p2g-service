package vip.yazilim.p2g.web.controller.rest.spotify;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vip.yazilim.libs.springcore.rest.model.RestResponse;
import vip.yazilim.p2g.web.config.annotation.HasSystemRole;
import vip.yazilim.p2g.web.constant.enums.Role;
import vip.yazilim.p2g.web.entity.UserDevice;
import vip.yazilim.p2g.web.service.p2g.IUserDeviceService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyUserService;
import vip.yazilim.p2g.web.util.SecurityHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static vip.yazilim.p2g.web.constant.Constants.API_SPOTIFY;

/**
 * @author mustafaarifsisman - 1.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@RestController
@RequestMapping(API_SPOTIFY + "/user")
public class SpotifyDeviceRest {

    @Autowired
    private ISpotifyUserService spotifyUserService;

    @Autowired
    private IUserDeviceService userDeviceService;

    @HasSystemRole(role = Role.P2G_USER)
    @GetMapping({"/device"})
    public RestResponse<List<UserDevice>> getUserDeviceList(HttpServletRequest request, HttpServletResponse response) {
        return RestResponse.generateResponse(spotifyUserService.getUsersAvailableDevices(SecurityHelper.getUserId()), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @PutMapping({"/device"})
    public RestResponse<UserDevice> saveUsersActiveDevice(HttpServletRequest request, HttpServletResponse response, @RequestBody UserDevice userDevice) {
        return RestResponse.generateResponse(userDeviceService.saveUsersActiveDevice(SecurityHelper.getUserId(), userDevice), HttpStatus.OK, request, response);
    }
}
