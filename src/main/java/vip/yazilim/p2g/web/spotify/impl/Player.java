package vip.yazilim.p2g.web.spotify.impl;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import vip.yazilim.p2g.web.constant.Constants;
import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.p2g.web.entity.SpotifyToken;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.service.IRoomService;
import vip.yazilim.p2g.web.service.ISongService;
import vip.yazilim.p2g.web.service.ITokenService;
import vip.yazilim.p2g.web.service.IUserService;
import vip.yazilim.p2g.web.spotify.IPlayer;
import vip.yazilim.spring.utils.exception.DatabaseException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Controller
public class Player implements IPlayer {

    private final Logger LOGGER = LoggerFactory.getLogger(Player.class);

    @Autowired
    @Qualifier(Constants.BEAN_NAME_AUTHORIZATION_CODE)
    private SpotifyApi spotifyApi;

    @Autowired
    private IUserService userService;

    @Autowired
    private ISongService songServices;

    @Autowired
    private ITokenService tokenService;

    @Override
    public boolean play(String roomUuid, String songUuid) {
        Song song;
        String songUri;
        List<SpotifyToken> spotifyTokenList;

        try {
            Optional<Song> songOpt = songServices.getById(songUuid);
            if (songOpt.isPresent()) {
                song = songOpt.get();
                songUri = song.getUri();
            } else {
                LOGGER.warn("Song not found with songUuid[{}]", songUuid);
                return false;
            }

            spotifyTokenList = tokenService.getTokenListByRoomUuid(roomUuid);
        } catch (DatabaseException e) {
            e.printStackTrace();
            return false;
        }

        String accessToken = null;
        try {
            for (SpotifyToken token : spotifyTokenList) {
                accessToken = token.getAccessToken();
                spotifyApi.setAccessToken(accessToken);
                spotifyApi.startResumeUsersPlayback()
                        .context_uri(songUri)
                        .build().execute();
            }
            return true;
        } catch (SpotifyWebApiException | IOException e) {
            LOGGER.error("An error occurred when playing songUuid[{}], roomUuid[{}], accessToken[{}]", songUuid, roomUuid
                    , accessToken);
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean play(String roomUuid) {
        return false;
    }

    @Override
    public boolean pause(String roomUuid) {
        return false;
    }

    @Override
    public boolean next(String roomUuid) {
        return false;
    }

    @Override
    public boolean previous(String roomUuid) {
        return false;
    }

    @Override
    public boolean seek(String roomUuid, Integer ms) {
        return false;
    }

    @Override
    public boolean shuffle(String roomUuid) {
        return false;
    }

    @Override
    public boolean repeat(String roomUuid) {
        return false;
    }
}
