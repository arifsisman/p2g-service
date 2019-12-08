package vip.yazilim.p2g.web.controller.rest.spotify;

import com.wrapper.spotify.model_objects.specification.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vip.yazilim.p2g.web.entity.relation.UserDevice;
import vip.yazilim.p2g.web.service.spotify.ISpotifyUserService;
import vip.yazilim.p2g.web.util.SecurityHelper;
import vip.yazilim.spring.core.exception.web.ServiceException;
import vip.yazilim.spring.core.rest.model.RestResponse;
import vip.yazilim.spring.core.util.RestResponseFactory;

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
public class SpotifyUserRest {

    @Autowired
    private ISpotifyUserService spotifyUserService;

    @GetMapping("/{spotifyAccountId}")
    public RestResponse<User> getSpotifyUser(HttpServletRequest request, HttpServletResponse response, @PathVariable String spotifyAccountId) {
        User spotifyUser;

        try {
            spotifyUser = spotifyUserService.getSpotifyUser(spotifyAccountId);
        } catch (Exception e) {
            throw new ServiceException(e);
        }

        return RestResponseFactory.generateResponse(spotifyUser, HttpStatus.OK, request, response);

    }

    @GetMapping("/current")
    public RestResponse<User> getCurrentSpotifyUser(HttpServletRequest request, HttpServletResponse response) {
        User spotifyUser;

        try {
            spotifyUser = spotifyUserService.getCurrentSpotifyUser(SecurityHelper.getUserUuid());
        } catch (Exception e) {
            throw new ServiceException(e);
        }

        return RestResponseFactory.generateResponse(spotifyUser, HttpStatus.OK, request, response);
    }

    @GetMapping("/{userUuid}/devices")
    public RestResponse<List<UserDevice>> getSpotifyUserDevices(HttpServletRequest request, HttpServletResponse response, @PathVariable String userUuid) {
        List<UserDevice> userDeviceList;

        try {
            userDeviceList = spotifyUserService.getUsersAvailableDevices(userUuid);
        } catch (Exception e) {
            throw new ServiceException(e);
        }

        return RestResponseFactory.generateResponse(userDeviceList, HttpStatus.OK, request, response);
    }
}
