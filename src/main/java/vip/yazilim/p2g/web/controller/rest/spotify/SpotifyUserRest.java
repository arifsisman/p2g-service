package vip.yazilim.p2g.web.controller.rest.spotify;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vip.yazilim.p2g.web.config.annotation.HasSystemRole;
import vip.yazilim.p2g.web.constant.Role;
import vip.yazilim.p2g.web.entity.UserDevice;
import vip.yazilim.p2g.web.service.spotify.ISpotifyUserService;
import vip.yazilim.p2g.web.util.SecurityHelper;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;
import vip.yazilim.spring.core.rest.model.RestResponse;
import vip.yazilim.spring.core.util.RestResponseFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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

    @HasSystemRole(role = Role.P2G_USER)
    @GetMapping("/{spotifyAccountId}")
    public RestResponse<User> getSpotifyUser(HttpServletRequest request, HttpServletResponse response, @PathVariable String spotifyAccountId) throws IOException, SpotifyWebApiException {
        return RestResponseFactory.generateResponse(spotifyUserService.getSpotifyUser(spotifyAccountId), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @GetMapping("/current")
    public RestResponse<User> getCurrentSpotifyUser(HttpServletRequest request, HttpServletResponse response) throws DatabaseException, IOException, SpotifyWebApiException {
        return RestResponseFactory.generateResponse(spotifyUserService.getCurrentSpotifyUser(SecurityHelper.getUserId()), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @GetMapping("/{userId}/devices")
    public RestResponse<List<UserDevice>> getSpotifyUserDevices(HttpServletRequest request, HttpServletResponse response, @PathVariable String userId) throws DatabaseException, IOException, SpotifyWebApiException {
        return RestResponseFactory.generateResponse(spotifyUserService.getUsersAvailableDevices(userId), HttpStatus.OK, request, response);
    }
}
