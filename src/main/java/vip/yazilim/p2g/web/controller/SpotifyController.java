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
import vip.yazilim.p2g.web.constant.Constants;
import vip.yazilim.p2g.web.constant.SearchTypes;
import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.p2g.web.entity.SpotifyToken;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.exception.TokenException;
import vip.yazilim.p2g.web.model.SearchModel;
import vip.yazilim.p2g.web.service.ISearchService;
import vip.yazilim.p2g.web.service.ITokenService;
import vip.yazilim.p2g.web.spotify.*;
import vip.yazilim.p2g.web.util.SecurityHelper;
import vip.yazilim.spring.utils.exception.DatabaseException;
import vip.yazilim.spring.utils.exception.InvalidUpdateException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.List;

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
    private IPlayer player;

    @Autowired
    private IProfile profile;

    @Autowired
    private ISearchService searchService;

    @Autowired
    private ITrack track;

    @Autowired
    private IAlbum album;

    @Autowired
    private IPlaylist playlist;

    @GetMapping("/authorize")
    public void authorize(HttpServletResponse httpServletResponse) {
        URI uri = authorizationCodeUriRequest.execute();

        httpServletResponse.setHeader("Location", uri.toString());
        httpServletResponse.setStatus(302);
    }

    @GetMapping("/callback")
    @ResponseBody
    public SpotifyToken callback(@RequestParam String code) throws DatabaseException, InvalidUpdateException {

        AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(code).build();

        final AuthorizationCodeCredentials authorizationCodeCredentials;

        try {
            authorizationCodeCredentials = authorizationCodeRequest.execute();
        } catch (IOException | SpotifyWebApiException e) {
            throw new TokenException("Error while fetching tokens!");
        }

        String accessToken = authorizationCodeCredentials.getAccessToken();
        String refreshToken = authorizationCodeCredentials.getRefreshToken();
        Integer expireTime = authorizationCodeCredentials.getExpiresIn();

        spotifyApi.setAccessToken(accessToken);
        spotifyApi.setRefreshToken(refreshToken);

//        LOGGER.info("Access Token: " + accessToken);
//        LOGGER.info("Refresh Token: " + refreshToken);
//        LOGGER.info("Expire time: " + expireTime);

        String userUuid = SecurityHelper.getUserUuid();
        SpotifyToken token = tokenService.saveUserToken(userUuid, accessToken, refreshToken);

        int delayMs = 55 * 60 * 1000;
        Date now = new Date();
        Date startDate = new Date(now.getTime() + delayMs);

        tokenRefreshScheduler.getScheduler()
                .scheduleWithFixedDelay(() -> tokenRefresher.refreshToken(userUuid), startDate, delayMs);

        return token;
    }

    @GetMapping("/play/{roomUuid}/{uri}")
    public void play(@PathVariable String roomUuid, @PathVariable String uri) {
//        player.play(roomUuid, uri);
        player.play("1", "spotify:album:5zT1JLIj9E57p3e1rFm9Uq");
    }

    @GetMapping("/spotify/search/{query}")
    public List<SearchModel> search(@PathVariable String query) {

        List<SearchModel> searchModelList = searchService.search(query, SearchTypes.TRACK, SearchTypes.ALBUM, SearchTypes.PLAYLIST);
//        List<SearchModel> searchModelList = searchService.search(query);

//        return searchService.search(query);

        for (SearchModel searchModel : searchModelList) {
            LOGGER.info(searchModel.getType().getType() + " - " +searchModel.getName());
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

        for (Song s:songList) {
            LOGGER.info(s.getName());
        }

        return songList;
    }

    @GetMapping("/spotify/user/{spotifyAccountId}")
    public User getUser(@PathVariable String spotifyAccountId) {
        User user;
        user = profile.getUser(spotifyAccountId);
        LOGGER.info("User Name: {}", user.getDisplayName());
        return user;

//        return profile.getUser(spotifyAccountId);
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
