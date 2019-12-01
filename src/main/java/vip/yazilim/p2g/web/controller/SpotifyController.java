package vip.yazilim.p2g.web.controller;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.enums.ModelObjectType;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import vip.yazilim.p2g.web.config.spotify.TokenRefreshScheduler;
import vip.yazilim.p2g.web.config.spotify.TokenRefresher;
import vip.yazilim.p2g.web.constant.Constants;
import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.entity.SpotifyToken;
import vip.yazilim.p2g.web.entity.relation.UserDevice;
import vip.yazilim.p2g.web.exception.RequestException;
import vip.yazilim.p2g.web.model.SearchModel;
import vip.yazilim.p2g.web.service.p2g.IRoomService;
import vip.yazilim.p2g.web.service.p2g.ITokenService;
import vip.yazilim.p2g.web.service.p2g.IUserService;
import vip.yazilim.p2g.web.service.p2g.relation.IUserDeviceService;
import vip.yazilim.p2g.web.service.spotify.*;
import vip.yazilim.p2g.web.util.SecurityHelper;
import vip.yazilim.spring.utils.exception.runtime.NotFoundException;
import vip.yazilim.spring.utils.exception.runtime.ServiceException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 23.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@RestController
public class SpotifyController {

    private final Logger LOGGER = LoggerFactory.getLogger(SpotifyController.class);

    @Autowired
    @Qualifier(Constants.BEAN_NAME_AUTHORIZATION_CODE)
    private SpotifyApi spotifyApi;

    @Autowired
    private AuthorizationCodeUriRequest authorizationCodeUriRequest;

    @Autowired
    private TokenRefreshScheduler tokenRefreshScheduler;

    @Autowired
    private TokenRefresher tokenRefresher;

    @Autowired
    private ITokenService tokenService;

    @Autowired
    private IRoomService roomService;

    @Autowired
    private IUserService userService;

    @Autowired
    private ISpotifyPlayerService spotifyPlayerService;

    @Autowired
    private ISpotifyProfileService spotifyProfileService;

    @Autowired
    private ISpotifyTrackService spotifyTrackService;

    @Autowired
    private ISpotifyAlbumService spotifyAlbumService;

    @Autowired
    private ISpotifyPlaylistService spotifyPlaylistService;

    @Autowired
    private ISpotifySearchService spotifySearchService;

    @Autowired
    private IUserDeviceService userDeviceService;

    @GetMapping("/authorize")
    public void authorize(HttpServletResponse httpServletResponse) {
        URI uri = authorizationCodeUriRequest.execute();

        httpServletResponse.setHeader("Location", uri.toString());
        httpServletResponse.setStatus(302);
    }

    @GetMapping("/callback")
    @ResponseBody
    public SpotifyToken callback(@RequestParam String code) {
        String userUuid = SecurityHelper.getUserUuid();
        Optional<vip.yazilim.p2g.web.entity.User> userOpt = userService.getUserByUuid(userUuid);

        if (!userOpt.isPresent()) {
            throw new NotFoundException();
        }

        AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(code).build();
        final AuthorizationCodeCredentials authorizationCodeCredentials;

        try {
            authorizationCodeCredentials = authorizationCodeRequest.execute();
        } catch (IOException | SpotifyWebApiException e) {
            throw new ServiceException("Error while fetching tokens!", e);
        }

        String accessToken = authorizationCodeCredentials.getAccessToken();
        String refreshToken = authorizationCodeCredentials.getRefreshToken();

        spotifyApi.setAccessToken(accessToken);
        spotifyApi.setRefreshToken(refreshToken);

//        LOGGER.info("Access Token: " + accessToken);
//        LOGGER.info("Refresh Token: " + refreshToken);
//        LOGGER.info("Expire time: " + expireTime);

        SpotifyToken token;

        try {
            token = tokenService.saveUserToken(userUuid, accessToken, refreshToken);
        } catch (Exception e) {
            throw new ServiceException(e);
        }

        // updates spotify infos every authorize
        try {
            userService.setSpotifyInfo(getCurrentSpotifyUser(), userOpt.get());
        } catch (Exception e) {
            throw new ServiceException(e);
        }

        // Set Token refresh scheduler
        int delayMs = 55 * 60 * 1000;
        Date now = new Date();
        Date startDate = new Date(now.getTime() + delayMs);

        tokenRefreshScheduler.getScheduler()
                .scheduleWithFixedDelay(() -> tokenRefresher.refreshToken(userUuid), startDate, delayMs);

        return token;
    }

    @GetMapping("/rooms")
    public List<Room> rooms() {
        try {
            return roomService.getAll();
        } catch (Exception e) {
            throw new ServiceException("An error occurred while getting rooms.", e);
        }
    }

    @GetMapping("/room/{roomUuid}/play/{uri}")
    public boolean play(@PathVariable String roomUuid, @PathVariable String uri) {
        try {
            spotifyPlayerService.play(roomUuid, uri);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return true;
    }

    @GetMapping("/spotify/search/{query}")
    public List<SearchModel> search(@PathVariable String query) {
        try {
            return spotifySearchService.search(query, ModelObjectType.TRACK, ModelObjectType.ALBUM, ModelObjectType.PLAYLIST);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @GetMapping("/spotify/song/{id}")
    public SearchModel getSong(@PathVariable String id) {
        try {
            return spotifyTrackService.getTrack(id);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @GetMapping("/spotify/songs/{ids}")
    public List<SearchModel> getSongList(@PathVariable String[] ids) {
        List<SearchModel> searchModelList;
        try {
            searchModelList = spotifyTrackService.getSeveralTracks(ids);
        } catch (RequestException e) {
            throw new ServiceException(e);
        }
        return searchModelList;
    }

    @GetMapping("/spotify/user/{spotifyAccountId}")
    public com.wrapper.spotify.model_objects.specification.User getSpotifyUser(@PathVariable String spotifyAccountId) {
        try {
            return spotifyProfileService.getSpotifyUser(spotifyAccountId);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @GetMapping("/spotify/user/current")
    public com.wrapper.spotify.model_objects.specification.User getCurrentSpotifyUser() {
        try {
            return spotifyProfileService.getCurrentSpotifyUser(SecurityHelper.getUserUuid());
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @GetMapping("/spotify/user/devices")
    public List<UserDevice> getSpotifyUserDevices() {
        try {
            return spotifyPlayerService.getUsersAvailableDevices(SecurityHelper.getUserUuid());
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @GetMapping("/user/devices")
    public List<UserDevice> getUserDevices() {
        try {
            return userDeviceService.getDevicesByUserUuid(SecurityHelper.getUserUuid());
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @GetMapping("/spotify/album/{albumId}/songs")
    public List<SearchModel> getAlbumSongList(@PathVariable String albumId) {
        try {
            return spotifyAlbumService.getSongs(albumId);
        } catch (RequestException e) {
            throw new ServiceException(e);
        }
    }

    @GetMapping("/spotify/playlist/{playlistId}/songs")
    public List<SearchModel> getPlaylistSongList(@PathVariable String playlistId) {
        try {
            return spotifyPlaylistService.getSongs(playlistId);
        } catch (RequestException e) {
            throw new ServiceException(e);
        }
    }
}
