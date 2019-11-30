package vip.yazilim.p2g.web.controller;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import vip.yazilim.p2g.web.config.spotify.TokenRefreshScheduler;
import vip.yazilim.p2g.web.config.spotify.TokenRefresher;
import vip.yazilim.p2g.web.constant.Constants;
import vip.yazilim.p2g.web.constant.SearchTypes;
import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.p2g.web.entity.SpotifyToken;
import vip.yazilim.p2g.web.exception.TokenException;
import vip.yazilim.p2g.web.model.SearchModel;
import vip.yazilim.p2g.web.service.p2g.IRoomService;
import vip.yazilim.p2g.web.service.p2g.ISearchService;
import vip.yazilim.p2g.web.service.p2g.ITokenService;
import vip.yazilim.p2g.web.service.p2g.IUserService;
import vip.yazilim.p2g.web.service.spotify.*;
import vip.yazilim.p2g.web.util.SecurityHelper;
import vip.yazilim.spring.utils.exception.DatabaseException;
import vip.yazilim.spring.utils.exception.InvalidUpdateException;
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
@Controller
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
    private ISPlayerService player;

    @Autowired
    private ISProfileService profile;

    @Autowired
    private ISearchService searchService;

    @Autowired
    private ISTrackService track;

    @Autowired
    private ISAlbumService album;

    @Autowired
    private ISPlaylistService playlist;

    @Autowired
    private ISRequestService request;

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
//        Integer expireTime = authorizationCodeCredentials.getExpiresIn();

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

        //TODO: check for existing users!!!
        // Set Spotify infos
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
        List<Room> roomList;
        try {
            roomList = roomService.getAll();

            for (Room room : roomList)
                LOGGER.info("Room Uuid [{}]", room.getUuid());
        } catch (Exception e) {
            LOGGER.error("An error occurred while getting rooms.");
            throw new ServiceException("Error while fetching tokens!", e);
        }
        return roomList;
    }

    @GetMapping("/room/{roomUuid}/play/{uri}")
    public boolean play(@PathVariable String roomUuid, @PathVariable String uri) {
        player.play(roomUuid, uri);
        return true;
    }

    @GetMapping("/spotify/search/{query}")
    public List<SearchModel> search(@PathVariable String query) {

        List<SearchModel> searchModelList = searchService.search(query, SearchTypes.TRACK, SearchTypes.ALBUM, SearchTypes.PLAYLIST);
//        List<SearchModel> searchModelList = searchService.search(query);

//        return searchService.search(query);

        for (SearchModel searchModel : searchModelList) {
            LOGGER.info(searchModel.getType().getType() + " - " + searchModel.getName());
        }

        return searchModelList;
    }

    @GetMapping("/spotify/song/{id}")
    public Song getSong(@PathVariable String id) {
        Song song;

//        return track.getTrack(id);
        song = track.getTrack(id);

        LOGGER.info(song.getName());

        return song;
    }

    @GetMapping("/spotify/songs/{ids}")
    public List<Song> getSongList(@PathVariable String[] ids) {
        List<Song> songList;

//        return track.getSeveralTracks(spotifyToken, ids);
        songList = track.getSeveralTracks(ids);

        for (Song s : songList) {
            LOGGER.info(s.getName());
        }

        return songList;
    }

    @GetMapping("/spotify/user/{spotifyAccountId}")
    public com.wrapper.spotify.model_objects.specification.User getSpotifyUser(@PathVariable String spotifyAccountId) {
        com.wrapper.spotify.model_objects.specification.User spotifyUser = profile.getSpotifyUser(spotifyAccountId);
        LOGGER.info("User Name: {}", spotifyUser.getDisplayName());
        return spotifyUser;

//        return profile.getSpotifyUser(spotifyAccountId);
    }

    @GetMapping("/spotify/user/current")
    public com.wrapper.spotify.model_objects.specification.User getCurrentSpotifyUser() {
        try {
            com.wrapper.spotify.model_objects.specification.User spotifyUser = profile.getCurrentSpotifyUser(SecurityHelper.getUserUuid());
            LOGGER.info("Spotify User Id: " + spotifyUser.getId());

            return spotifyUser;
        } catch (Exception e) {
            LOGGER.error("An error occurred while getting user.");
            throw new ServiceException(e);
        }
    }

    @GetMapping("/spotify/album/{albumId}/songs")
    public List<Song> getAlbumSongList(@PathVariable String albumId) {
        List<Song> songList;

//        return album.getAlbumSongs(albumId);
        songList = album.getSongs(albumId);

        LOGGER.info(String.valueOf(songList.size()));

        return songList;
    }

    @GetMapping("/spotify/playlist/{playlistId}/songs")
    public List<Song> getPlaylistSongList(@PathVariable String playlistId) {
        List<Song> songList;

//        return playlist.getSongs(playlistId);
        songList = playlist.getSongs(playlistId);

        LOGGER.info(String.valueOf(songList.size()));

        return songList;
    }
}
