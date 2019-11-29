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
import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.p2g.web.entity.SpotifyToken;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.exception.TokenException;
import vip.yazilim.p2g.web.model.SearchModel;
import vip.yazilim.p2g.web.service.ISearchService;
import vip.yazilim.p2g.web.service.ITokenService;
import vip.yazilim.p2g.web.spotify.IPlayer;
import vip.yazilim.p2g.web.spotify.IProfile;
import vip.yazilim.p2g.web.spotify.ITrack;
import vip.yazilim.p2g.web.util.SecurityHelper;
import vip.yazilim.spring.utils.exception.DatabaseException;
import vip.yazilim.spring.utils.exception.InvalidUpdateException;

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
    private IPlayer player;

    @Autowired
    private IProfile profile;

    @Autowired
    private ISearchService searchService;

    @Autowired
    private ITrack track;

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

    @GetMapping("/search/{userUuid}/{query}")
    public List<SearchModel> search(@PathVariable String userUuid, @PathVariable String query) {

        SpotifyToken spotifyToken = null;

        try {
            Optional<SpotifyToken> spotifyTokenOptional = tokenService.getTokenByUserUuid(userUuid);
            if (spotifyTokenOptional.isPresent())
                spotifyToken = spotifyTokenOptional.get();
        } catch (DatabaseException e) {
            LOGGER.error("Token not found for userUuid[{}]", userUuid);
        }

//        List<SearchModel> searchModelList = searchService.search(spotifyToken, query, SearchTypes.TRACK);
        List<SearchModel> searchModelList = searchService.search(spotifyToken, query);

//        return searchService.search(spotifyToken, query);

        for (SearchModel searchModel : searchModelList) {
            LOGGER.info(searchModel.getType().getType());
            LOGGER.info(searchModel.getName());
        }

        return searchModelList;
    }

    @GetMapping("/song/{userUuid}/{id}")
    public Song getSong(@PathVariable String userUuid, @PathVariable String id) {
        Song song;
        SpotifyToken spotifyToken = null;

        try {
            Optional<SpotifyToken> spotifyTokenOptional = tokenService.getTokenByUserUuid(userUuid);
            if (spotifyTokenOptional.isPresent())
                spotifyToken = spotifyTokenOptional.get();
        } catch (DatabaseException e) {
            LOGGER.error("Token not found for userUuid[{}]", userUuid);
        }

//        return track.getTrack(spotifyToken, id);
        song = track.getTrack(spotifyToken, id);

        LOGGER.info(song.getName());

        return song;
    }

    @GetMapping("/songs/{userUuid}/{ids}")
    public List<Song> getSongList(@PathVariable String userUuid, @PathVariable String[] ids) {
        List<Song> songList;
        SpotifyToken spotifyToken = null;

        try {
            Optional<SpotifyToken> spotifyTokenOptional = tokenService.getTokenByUserUuid(userUuid);
            if (spotifyTokenOptional.isPresent())
                spotifyToken = spotifyTokenOptional.get();
        } catch (DatabaseException e) {
            LOGGER.error("Token not found for userUuid[{}]", userUuid);
        }

//        return track.getSeveralTracks(spotifyToken, ids);
        songList = track.getSeveralTracks(spotifyToken, ids);

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
}
