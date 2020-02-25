package vip.yazilim.p2g.web.controller.rest.spotify;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vip.yazilim.p2g.web.config.annotation.HasRoomPrivilege;
import vip.yazilim.p2g.web.config.annotation.UpdateRoomSongs;
import vip.yazilim.p2g.web.constant.enums.Privilege;
import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.p2g.web.service.spotify.ISpotifyPlayerService;
import vip.yazilim.spring.core.exception.InvalidArgumentException;
import vip.yazilim.spring.core.exception.database.DatabaseException;
import vip.yazilim.spring.core.rest.model.RestResponse;
import vip.yazilim.spring.core.rest.model.RestResponseFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static vip.yazilim.p2g.web.constant.Constants.API_SPOTIFY;

/**
 * @author mustafaarifsisman - 1.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@RestController
@RequestMapping(API_SPOTIFY + "/player")
public class PlayerRest {

    @Autowired
    private ISpotifyPlayerService spotifyPlayerService;

    @HasRoomPrivilege(privilege = Privilege.SONG_CONTROL)
    @UpdateRoomSongs
    @PostMapping("/play")
    public RestResponse<Boolean> playSong(HttpServletRequest request, HttpServletResponse response, @RequestBody Song song) throws InvalidArgumentException, SpotifyWebApiException, IOException, DatabaseException {
        return RestResponseFactory.generateResponse(spotifyPlayerService.roomPlay(song, 0), HttpStatus.OK, request, response);
    }

    @HasRoomPrivilege(privilege = Privilege.SONG_CONTROL)
    @UpdateRoomSongs
    @PostMapping("/{roomId}/play")
    public RestResponse<Boolean> startResume(HttpServletRequest request, HttpServletResponse response, @PathVariable Long roomId) throws InvalidArgumentException, SpotifyWebApiException, IOException, DatabaseException {
        return RestResponseFactory.generateResponse(spotifyPlayerService.roomStartResume(roomId), HttpStatus.OK, request, response);
    }

    @HasRoomPrivilege(privilege = Privilege.SONG_CONTROL)
    @UpdateRoomSongs
    @PostMapping("/{roomId}/pause")
    public RestResponse<Boolean> pause(HttpServletRequest request, HttpServletResponse response, @PathVariable Long roomId) throws InvalidArgumentException, SpotifyWebApiException, IOException, DatabaseException {
        return RestResponseFactory.generateResponse(spotifyPlayerService.roomPause(roomId), HttpStatus.OK, request, response);
    }

    @HasRoomPrivilege(privilege = Privilege.SONG_CONTROL)
    @UpdateRoomSongs
    @PostMapping("/{roomId}/next")
    public RestResponse<Boolean> next(HttpServletRequest request, HttpServletResponse response, @PathVariable Long roomId) throws InvalidArgumentException, SpotifyWebApiException, IOException, DatabaseException {
        return RestResponseFactory.generateResponse(spotifyPlayerService.roomNext(roomId), HttpStatus.OK, request, response);
    }

    @HasRoomPrivilege(privilege = Privilege.SONG_CONTROL)
    @UpdateRoomSongs
    @PostMapping("/{roomId}/previous")
    public RestResponse<Boolean> previous(HttpServletRequest request, HttpServletResponse response, @PathVariable Long roomId) throws InvalidArgumentException, SpotifyWebApiException, IOException, DatabaseException {
        return RestResponseFactory.generateResponse(spotifyPlayerService.roomPrevious(roomId), HttpStatus.OK, request, response);
    }

    @HasRoomPrivilege(privilege = Privilege.SONG_CONTROL)
    @UpdateRoomSongs
    @PostMapping("/{roomId}/seek/{ms}")
    public RestResponse<Boolean> seek(HttpServletRequest request, HttpServletResponse response, @PathVariable Long roomId, @PathVariable Integer ms) throws InvalidArgumentException, SpotifyWebApiException, IOException, DatabaseException {
        return RestResponseFactory.generateResponse(spotifyPlayerService.roomSeek(roomId, ms), HttpStatus.OK, request, response);
    }

    @HasRoomPrivilege(privilege = Privilege.SONG_CONTROL)
    @UpdateRoomSongs
    @PostMapping("/{roomId}/repeat")
    public RestResponse<Boolean> repeat(HttpServletRequest request, HttpServletResponse response, @PathVariable Long roomId) throws InvalidArgumentException, SpotifyWebApiException, IOException, DatabaseException {
        return RestResponseFactory.generateResponse(spotifyPlayerService.roomRepeat(roomId), HttpStatus.OK, request, response);
    }
}
