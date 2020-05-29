package vip.yazilim.p2g.web.rest;

import com.wrapper.spotify.enums.ProductType;
import com.wrapper.spotify.model_objects.specification.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import vip.yazilim.libs.springcore.rest.model.RestResponse;
import vip.yazilim.p2g.web.config.OnlineStatusScheduler;
import vip.yazilim.p2g.web.config.annotation.HasRoomPrivilege;
import vip.yazilim.p2g.web.config.annotation.HasSystemRole;
import vip.yazilim.p2g.web.config.annotation.UpdateRoomSongs;
import vip.yazilim.p2g.web.entity.*;
import vip.yazilim.p2g.web.enums.OnlineStatus;
import vip.yazilim.p2g.web.enums.Privilege;
import vip.yazilim.p2g.web.enums.Role;
import vip.yazilim.p2g.web.exception.SpotifyException;
import vip.yazilim.p2g.web.model.SearchModel;
import vip.yazilim.p2g.web.service.p2g.*;
import vip.yazilim.p2g.web.service.spotify.IPlayerService;
import vip.yazilim.p2g.web.service.spotify.ISpotifySearchService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyUserService;
import vip.yazilim.p2g.web.util.SecurityHelper;
import vip.yazilim.p2g.web.util.TimeHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static vip.yazilim.p2g.web.constant.Constants.API_SPOTIFY;

/**
 * @author mustafaarifsisman - 15.04.2020
 * @contact mustafaarifsisman@gmail.com
 */
@RestController
@RequestMapping(API_SPOTIFY)
public class SpotifyRest {

    private final ISpotifyTokenService tokenService;
    private final IUserService userService;
    private final ISpotifyUserService spotifyUserService;
    private final IRoomUserService roomUserService;
    private final IRoomService roomService;
    private final IUserDeviceService userDeviceService;
    private final OnlineStatusScheduler onlineStatusScheduler;
    private final ISpotifySearchService spotifySearchService;
    private final IPlayerService spotifyPlayerService;
    private Logger logger = LoggerFactory.getLogger(SpotifyRest.class);

    public SpotifyRest(ISpotifyTokenService tokenService, IUserService userService, ISpotifyUserService spotifyUserService, IRoomUserService roomUserService, IRoomService roomService, IUserDeviceService userDeviceService, OnlineStatusScheduler onlineStatusScheduler, ISpotifySearchService spotifySearchService, IPlayerService spotifyPlayerService) {
        this.tokenService = tokenService;
        this.userService = userService;
        this.spotifyUserService = spotifyUserService;
        this.roomUserService = roomUserService;
        this.roomService = roomService;
        this.userDeviceService = userDeviceService;
        this.onlineStatusScheduler = onlineStatusScheduler;
        this.spotifySearchService = spotifySearchService;
        this.spotifyPlayerService = spotifyPlayerService;
    }

    @GetMapping("/login")
    @Transactional
    public RestResponse<User> login(HttpServletRequest request, HttpServletResponse response) {
        String userId = SecurityHelper.getUserId();
        com.wrapper.spotify.model_objects.specification.User spotifyUser = spotifyUserService.getCurrentSpotifyUser();

        Optional<User> userOpt = userService.getById(userId);
        if (userOpt.isPresent()) {
            return RestResponse.generateResponse(saveUser(spotifyUser, userOpt.get()), HttpStatus.OK, request, response);
        } else {
            User newUser = new User();
            newUser.setCreationDate(TimeHelper.getLocalDateTimeNow());
            newUser.setSystemRole(Role.P2G_USER.getRoleName());
            return RestResponse.generateResponse(saveUser(spotifyUser, newUser), HttpStatus.OK, request, response);
        }
    }

    @HasSystemRole(role = Role.P2G_USER)
    @PostMapping("/logout")
    public RestResponse<Boolean> logout(HttpServletRequest request, HttpServletResponse response) {
        String userId = SecurityHelper.getUserId();
        Optional<RoomUser> roomUserOpt = roomUserService.getRoomUserByUserId(userId);

        Optional<User> userOpt = userService.getById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setOnlineStatus(OnlineStatus.OFFLINE.name());
            userService.update(user);
        }

        logger.info("[{}] :: Logged out", userId);

        if (roomUserOpt.isPresent()) {
            RoomUser roomUser = roomUserOpt.get();
            Optional<Room> roomOpt = roomService.getRoomByUserId(userId);
            if (roomOpt.isPresent()) {
                Room room = roomOpt.get();
                if (room.getOwnerId().equals(userId)) {
                    return RestResponse.generateResponse(roomService.delete(room), HttpStatus.OK, request, response);
                } else {
                    return RestResponse.generateResponse(roomUserService.delete(roomUser), HttpStatus.OK, request, response);
                }
            }
        }

        return RestResponse.generateResponse(true, HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @PutMapping("/token")
    public RestResponse<String> updateUserAccessToken(HttpServletRequest request, HttpServletResponse response, @RequestBody String accessToken) {
        return RestResponse.generateResponse(tokenService.saveUserToken(SecurityHelper.getUserId(), accessToken), HttpStatus.OK, request, response);
    }

    private User saveUser(com.wrapper.spotify.model_objects.specification.User spotifyUser, User user) {
        if (!isAccountPremium(spotifyUser)) {
            throw new SpotifyException("Spotify account must be premium");
        }

        String userId = SecurityHelper.getUserId();

        user.setId(userId);
        user.setName(spotifyUser.getDisplayName());
        user.setEmail(spotifyUser.getEmail());
        user.setCountryCode(spotifyUser.getCountry().name());
        user.setOnlineStatus(OnlineStatus.ONLINE.name());
        user.setLastLogin(TimeHelper.getLocalDateTimeNow());

        Image[] images = spotifyUser.getImages();
        if (images.length > 0) {
            user.setImageUrl(images[0].getUrl());
        }

        // delete old or inactive device if present
        userDeviceService.getUsersActiveDevice(userId).ifPresent(userDeviceService::delete);

        List<UserDevice> userDeviceList = spotifyUserService.getUsersAvailableDevices(userId);
        boolean userDeviceCreated = false;

        for (UserDevice userDevice : userDeviceList) {
            if (userDevice.getActiveFlag()) {
                userDeviceService.create(userDevice);
                userDeviceCreated = true;
            }
        }

        if (!userDeviceCreated) {
            UserDevice userDevice = userDeviceList.get(0);
            userDevice.setActiveFlag(true);
            userDeviceService.create(userDevice);
        }

        tokenService.saveUserToken(userId, SecurityHelper.getUserAccessToken());
        User savedUser = userService.save(user);

        goOfflineAfterOneHour(userId);
        logger.info("[{}] :: Logged in", userId);

        return savedUser;
    }

    private boolean isAccountPremium(com.wrapper.spotify.model_objects.specification.User spotifyUser) {
        return spotifyUser.getProduct().getType().equals(ProductType.PREMIUM.getType());
    }

    private void goOfflineAfterOneHour(String userId) {
        int delayMs = 60 * 60 * 1000;

        onlineStatusScheduler.getScheduler()
                .scheduleWithFixedDelay(() -> userService.getById(userId).ifPresent(user -> {
                    if (ChronoUnit.MILLIS.between(user.getLastLogin(), TimeHelper.getLocalDateTimeNow()) >= delayMs) {
                        user.setOnlineStatus(OnlineStatus.OFFLINE.name());
                        userService.update(user);
                        logger.info("[{}] :: Logged out :: SYSTEM)", userId);
                    }
                }), TimeHelper.getDatePostponed(delayMs), Long.MAX_VALUE);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @GetMapping({"/user/device"})
    public RestResponse<List<UserDevice>> getUserDeviceList(HttpServletRequest request, HttpServletResponse response) {
        return RestResponse.generateResponse(spotifyUserService.getUsersAvailableDevices(SecurityHelper.getUserId()), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @PutMapping({"/user/device"})
    public RestResponse<UserDevice> changeUsersActiveDevice(HttpServletRequest request, HttpServletResponse response, @RequestBody UserDevice userDevice) {
        return RestResponse.generateResponse(userDeviceService.changeUsersActiveDevice(SecurityHelper.getUserId(), userDevice), HttpStatus.OK, request, response);
    }

    @HasRoomPrivilege(privilege = Privilege.SONG_SEARCH)
    @GetMapping("/search/{query}")
    public RestResponse<List<SearchModel>> search(HttpServletRequest request, HttpServletResponse response, @PathVariable String query) {
        return RestResponse.generateResponse(spotifySearchService.search(query), HttpStatus.OK, request, response);
    }

    @GetMapping("/search/recommendations")
    public RestResponse<List<SearchModel>> getRecommendations(HttpServletRequest request, HttpServletResponse response) {
        return RestResponse.generateResponse(spotifySearchService.getRecommendations(), HttpStatus.OK, request, response);
    }

    @HasRoomPrivilege(privilege = Privilege.SONG_SEARCH)
    @GetMapping("/search/song/{id}")
    public RestResponse<SearchModel> getSong(HttpServletRequest request, HttpServletResponse response, @PathVariable String id) {
        return RestResponse.generateResponse(spotifySearchService.getByTrackId(id), HttpStatus.OK, request, response);
    }

    @HasRoomPrivilege(privilege = Privilege.SONG_SEARCH)
    @GetMapping("/search/album/{albumId}/songs")
    public RestResponse<List<SearchModel>> getAlbumSongList(HttpServletRequest request, HttpServletResponse response, @PathVariable String albumId) {
        return RestResponse.generateResponse(spotifySearchService.getByAlbumId(albumId), HttpStatus.OK, request, response);
    }

    @HasRoomPrivilege(privilege = Privilege.SONG_SEARCH)
    @GetMapping("/search/playlist/{playlistId}/songs")
    public RestResponse<List<SearchModel>> getPlaylistSongList(HttpServletRequest request, HttpServletResponse response, @PathVariable String playlistId) {
        return RestResponse.generateResponse(spotifySearchService.getByPlaylistId(playlistId), HttpStatus.OK, request, response);
    }

    @HasRoomPrivilege(privilege = Privilege.SONG_CONTROL)
    @UpdateRoomSongs
    @PostMapping("/room/play")
    public RestResponse<Boolean> playSong(HttpServletRequest request, HttpServletResponse response, @RequestBody Song song) {
        return RestResponse.generateResponse(spotifyPlayerService.roomPlay(song, 0, true), HttpStatus.OK, request, response);
    }

    @HasRoomPrivilege(privilege = Privilege.SONG_CONTROL)
    @UpdateRoomSongs
    @PostMapping("/room/{roomId}/playPause")
    public RestResponse<Boolean> playPause(HttpServletRequest request, HttpServletResponse response, @PathVariable Long roomId) {
        return RestResponse.generateResponse(spotifyPlayerService.roomPlayPause(roomId), HttpStatus.OK, request, response);
    }

    @HasRoomPrivilege(privilege = Privilege.SONG_CONTROL)
    @UpdateRoomSongs
    @PostMapping("/room/{roomId}/next")
    public RestResponse<Boolean> next(HttpServletRequest request, HttpServletResponse response, @PathVariable Long roomId) {
        return RestResponse.generateResponse(spotifyPlayerService.roomNext(roomId), HttpStatus.OK, request, response);
    }

    @HasRoomPrivilege(privilege = Privilege.SONG_CONTROL)
    @UpdateRoomSongs
    @PostMapping("/room/{roomId}/previous")
    public RestResponse<Boolean> previous(HttpServletRequest request, HttpServletResponse response, @PathVariable Long roomId) {
        return RestResponse.generateResponse(spotifyPlayerService.roomPrevious(roomId), HttpStatus.OK, request, response);
    }

    @HasRoomPrivilege(privilege = Privilege.SONG_CONTROL)
    @UpdateRoomSongs
    @PostMapping("/room/{roomId}/seek/{ms}")
    public RestResponse<Boolean> seek(HttpServletRequest request, HttpServletResponse response, @PathVariable Long roomId, @PathVariable Integer ms) {
        return RestResponse.generateResponse(spotifyPlayerService.roomSeek(roomId, ms), HttpStatus.OK, request, response);
    }

    @HasRoomPrivilege(privilege = Privilege.SONG_CONTROL)
    @UpdateRoomSongs
    @PostMapping("/room/{roomId}/repeat")
    public RestResponse<Boolean> repeat(HttpServletRequest request, HttpServletResponse response, @PathVariable Long roomId) {
        return RestResponse.generateResponse(spotifyPlayerService.roomRepeat(roomId), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @PostMapping("/room/sync")
    public RestResponse<Boolean> sync(HttpServletRequest request, HttpServletResponse response) {
        return RestResponse.generateResponse(spotifyPlayerService.syncWithRoom(SecurityHelper.getUserId()), HttpStatus.OK, request, response);
    }
}
