package vip.yazilim.p2g.web.controller.rest.spotify;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vip.yazilim.p2g.web.entity.OAuthToken;
import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.entity.RoomUser;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.service.p2g.IRoomService;
import vip.yazilim.p2g.web.service.p2g.IRoomUserService;
import vip.yazilim.p2g.web.service.p2g.ISpotifyTokenService;
import vip.yazilim.p2g.web.service.p2g.IUserService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyUserService;
import vip.yazilim.p2g.web.util.SecurityHelper;
import vip.yazilim.spring.core.exception.GeneralException;
import vip.yazilim.spring.core.rest.model.RestResponse;
import vip.yazilim.spring.core.rest.model.RestResponseFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static vip.yazilim.p2g.web.constant.Constants.API_SPOTIFY;

/**
 * @author mustafaarifsisman - 23.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@RestController
@RequestMapping(API_SPOTIFY)
public class AuthorizationRest {

    private Logger LOGGER = LoggerFactory.getLogger(AuthorizationRest.class);

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

    @GetMapping("/login")
    public RestResponse<User> login(HttpServletRequest request, HttpServletResponse response) throws GeneralException, IOException, SpotifyWebApiException {
        String userId = SecurityHelper.getUserId();
        String userName = SecurityHelper.getUserDisplayName();
        Optional<User> userOpt = userService.getById(userId);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            LOGGER.info("{}[{}] logged in", userName, userId);
            updateUserAccessToken();
            return RestResponseFactory.generateResponse(updateUserSpotifyInfos(user), HttpStatus.OK, request, response);
        } else {
            return register(request, response);
        }
    }

    @PostMapping("/logout")
    public RestResponse<Boolean> logout(HttpServletRequest request, HttpServletResponse response) throws GeneralException {
        String userId = SecurityHelper.getUserId();
        Optional<RoomUser> roomUserOpt = roomUserService.getRoomUser(userId);

        if (roomUserOpt.isPresent()) {
            RoomUser roomUser = roomUserOpt.get();
            Optional<Room> roomOpt = roomService.getRoomByUserId(userId);
            if (roomOpt.isPresent()) {
                Room room = roomOpt.get();
                if (room.getOwnerId().equals(userId)) {
                    return RestResponseFactory.generateResponse(roomService.delete(room), HttpStatus.OK, request, response);
                } else {
                    return RestResponseFactory.generateResponse(roomUserService.delete(roomUser), HttpStatus.OK, request, response);
                }
            }
        }

        return RestResponseFactory.generateResponse(true, HttpStatus.OK, request, response);
    }

    @PostMapping("/token")
    public RestResponse<OAuthToken> updateUserAccessToken(HttpServletRequest request, HttpServletResponse response, @RequestBody String accessToken) throws GeneralException {
        String userId = SecurityHelper.getUserId();
        return RestResponseFactory.generateResponse(tokenService.saveUserToken(userId, accessToken), HttpStatus.OK, request, response);
    }

    private RestResponse<User> register(HttpServletRequest request, HttpServletResponse response) throws GeneralException, IOException, SpotifyWebApiException {
        String userId = SecurityHelper.getUserId();
        String email = SecurityHelper.getUserEmail();
        String userName = SecurityHelper.getUserDisplayName();

        User user = userService.createUser(userId, email, userName, "0");

        LOGGER.info("{}[{}] registered", userName, userId);
        updateUserAccessToken();
        return RestResponseFactory.generateResponse(updateUserSpotifyInfos(user), HttpStatus.OK, request, response);
    }

    private User updateUserSpotifyInfos(User user) throws GeneralException, IOException, SpotifyWebApiException {
        return userService.setSpotifyInfo(spotifyUserService.getCurrentSpotifyUser(user.getId()), user);
    }

    private void updateUserAccessToken() throws GeneralException {
        tokenService.saveUserToken(SecurityHelper.getUserId(), SecurityHelper.getUserAccessToken());
    }

}