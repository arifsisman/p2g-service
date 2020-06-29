package vip.yazilim.p2g.web.rest;

import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import vip.yazilim.libs.springcore.rest.model.RestResponse;
import vip.yazilim.p2g.web.config.annotation.HasSystemRole;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.enums.Role;
import vip.yazilim.p2g.web.service.p2g.ISpotifyTokenService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyUserService;
import vip.yazilim.p2g.web.util.SecurityHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static vip.yazilim.p2g.web.constant.Constants.API_SPOTIFY;

/**
 * @author mustafaarifsisman - 15.04.2020
 * @contact mustafaarifsisman@gmail.com
 */
@RestController
@RequestMapping(API_SPOTIFY)
public class SpotifyRestController {

    private final ISpotifyTokenService tokenService;
    private final ISpotifyUserService spotifyUserService;

    public SpotifyRestController(ISpotifyTokenService tokenService, ISpotifyUserService spotifyUserService) {
        this.tokenService = tokenService;
        this.spotifyUserService = spotifyUserService;
    }

    @GetMapping("/login")
    @Transactional
    public RestResponse<User> login(HttpServletRequest request, HttpServletResponse response) {
        return RestResponse.generateResponse(spotifyUserService.login(), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @PostMapping("/logout")
    public RestResponse<Boolean> logout(HttpServletRequest request, HttpServletResponse response) {
        return RestResponse.generateResponse(spotifyUserService.logout(), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @PutMapping("/token")
    public RestResponse<String> updateUserAccessToken(HttpServletRequest request, HttpServletResponse response, @RequestBody String accessToken) {
        return RestResponse.generateResponse(tokenService.saveUserToken(SecurityHelper.getUserId(), accessToken), HttpStatus.OK, request, response);
    }

}
