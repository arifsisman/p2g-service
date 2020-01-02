package vip.yazilim.p2g.web.controller.rest.spotify;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vip.yazilim.p2g.web.config.annotation.HasRoomPrivilege;
import vip.yazilim.p2g.web.constant.Privilege;
import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.p2g.web.service.spotify.ISpotifyPlayerService;
import vip.yazilim.spring.core.exception.general.InvalidArgumentException;
import vip.yazilim.spring.core.exception.general.InvalidUpdateException;
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
@RequestMapping(API_SPOTIFY + "/player")
public class PlayerRest {

    @Autowired
    private ISpotifyPlayerService spotifyPlayerService;

    @HasRoomPrivilege(privilege = Privilege.SONG_CONTROL)
    @PostMapping("/play")
    public RestResponse<List<Song>> playSong(HttpServletRequest request, HttpServletResponse response, @RequestBody Song song) throws InvalidUpdateException, InvalidArgumentException, SpotifyWebApiException, IOException, DatabaseException {
        return RestResponseFactory.generateResponse(spotifyPlayerService.roomPlay(song, 0), HttpStatus.OK, request, response);
    }

    @HasRoomPrivilege(privilege = Privilege.SONG_CONTROL)
    @PostMapping("/{roomUuid}/play")
    public RestResponse<List<Song>> startResume(HttpServletRequest request, HttpServletResponse response, @PathVariable Long roomUuid) throws InvalidUpdateException, InvalidArgumentException, SpotifyWebApiException, IOException, DatabaseException {
        return RestResponseFactory.generateResponse(spotifyPlayerService.roomStartResume(roomUuid), HttpStatus.OK, request, response);
    }

    @HasRoomPrivilege(privilege = Privilege.SONG_CONTROL)
    @PostMapping("/{roomUuid}/pause")
    public RestResponse<List<Song>> pause(HttpServletRequest request, HttpServletResponse response, @PathVariable Long roomUuid) throws InvalidUpdateException, InvalidArgumentException, SpotifyWebApiException, IOException, DatabaseException {
        return RestResponseFactory.generateResponse(spotifyPlayerService.roomPause(roomUuid), HttpStatus.OK, request, response);
    }

    @HasRoomPrivilege(privilege = Privilege.SONG_CONTROL)
    @PostMapping("/{roomUuid}/next")
    public RestResponse<List<Song>> next(HttpServletRequest request, HttpServletResponse response, @PathVariable Long roomUuid) throws InvalidUpdateException, InvalidArgumentException, SpotifyWebApiException, IOException, DatabaseException {
        return RestResponseFactory.generateResponse(spotifyPlayerService.roomNext(roomUuid), HttpStatus.OK, request, response);
    }

    @HasRoomPrivilege(privilege = Privilege.SONG_CONTROL)
    @PostMapping("/{roomUuid}/previous")
    public RestResponse<List<Song>> previous(HttpServletRequest request, HttpServletResponse response, @PathVariable Long roomUuid) throws InvalidUpdateException, InvalidArgumentException, SpotifyWebApiException, IOException, DatabaseException {
        return RestResponseFactory.generateResponse(spotifyPlayerService.roomPrevious(roomUuid), HttpStatus.OK, request, response);
    }

    @HasRoomPrivilege(privilege = Privilege.SONG_CONTROL)
    @PostMapping("/{roomUuid}/seek/{ms}")
    public RestResponse<Integer> seek(HttpServletRequest request, HttpServletResponse response, @PathVariable Long roomUuid, @PathVariable Integer ms) throws InvalidArgumentException, SpotifyWebApiException, IOException, DatabaseException {
        return RestResponseFactory.generateResponse(spotifyPlayerService.roomSeek(roomUuid, ms), HttpStatus.OK, request, response);
    }

    @HasRoomPrivilege(privilege = Privilege.SONG_CONTROL)
    @PostMapping("/{roomUuid}/repeat")
    public RestResponse<Boolean> repeat(HttpServletRequest request, HttpServletResponse response, @PathVariable Long roomUuid) throws InvalidUpdateException, InvalidArgumentException, SpotifyWebApiException, IOException, DatabaseException {
        return RestResponseFactory.generateResponse(spotifyPlayerService.roomRepeat(roomUuid), HttpStatus.OK, request, response);
    }
}
