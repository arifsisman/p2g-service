package vip.yazilim.p2g.web.controller.rest.spotify;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vip.yazilim.libs.springcore.rest.model.RestResponse;
import vip.yazilim.p2g.web.config.annotation.HasRoomPrivilege;
import vip.yazilim.p2g.web.config.annotation.HasSystemRole;
import vip.yazilim.p2g.web.config.annotation.UpdateRoomSongs;
import vip.yazilim.p2g.web.constant.enums.Privilege;
import vip.yazilim.p2g.web.constant.enums.Role;
import vip.yazilim.p2g.web.entity.RoomUser;
import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.p2g.web.service.spotify.ISpotifyPlayerService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static vip.yazilim.p2g.web.constant.Constants.API_SPOTIFY;

/**
 * @author mustafaarifsisman - 1.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@RestController
@RequestMapping(API_SPOTIFY + "/room")
public class SpotifyRoomPlayerRest {

    @Autowired
    private ISpotifyPlayerService spotifyPlayerService;

    @HasRoomPrivilege(privilege = Privilege.SONG_CONTROL)
    @UpdateRoomSongs
    @PostMapping("/play")
    public RestResponse<Boolean> playSong(HttpServletRequest request, HttpServletResponse response, @RequestBody Song song) {
        return RestResponse.generateResponse(spotifyPlayerService.roomPlay(song, 0, true), HttpStatus.OK, request, response);
    }

    @HasRoomPrivilege(privilege = Privilege.SONG_CONTROL)
    @UpdateRoomSongs
    @PostMapping("/{roomId}/playPause")
    public RestResponse<Boolean> playPause(HttpServletRequest request, HttpServletResponse response, @PathVariable Long roomId) {
        return RestResponse.generateResponse(spotifyPlayerService.roomPlayPause(roomId), HttpStatus.OK, request, response);
    }

    @HasRoomPrivilege(privilege = Privilege.SONG_CONTROL)
    @UpdateRoomSongs
    @PostMapping("/{roomId}/next")
    public RestResponse<Boolean> next(HttpServletRequest request, HttpServletResponse response, @PathVariable Long roomId) {
        return RestResponse.generateResponse(spotifyPlayerService.roomNext(roomId), HttpStatus.OK, request, response);
    }

    @HasRoomPrivilege(privilege = Privilege.SONG_CONTROL)
    @UpdateRoomSongs
    @PostMapping("/{roomId}/previous")
    public RestResponse<Boolean> previous(HttpServletRequest request, HttpServletResponse response, @PathVariable Long roomId) {
        return RestResponse.generateResponse(spotifyPlayerService.roomPrevious(roomId), HttpStatus.OK, request, response);
    }

    @HasRoomPrivilege(privilege = Privilege.SONG_CONTROL)
    @UpdateRoomSongs
    @PostMapping("/{roomId}/seek/{ms}")
    public RestResponse<Boolean> seek(HttpServletRequest request, HttpServletResponse response, @PathVariable Long roomId, @PathVariable Integer ms) {
        return RestResponse.generateResponse(spotifyPlayerService.roomSeek(roomId, ms), HttpStatus.OK, request, response);
    }

    @HasRoomPrivilege(privilege = Privilege.SONG_CONTROL)
    @UpdateRoomSongs
    @PostMapping("/{roomId}/repeat")
    public RestResponse<Boolean> repeat(HttpServletRequest request, HttpServletResponse response, @PathVariable Long roomId) {
        return RestResponse.generateResponse(spotifyPlayerService.roomRepeat(roomId), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @PostMapping("/sync")
    public RestResponse<Boolean> sync(HttpServletRequest request, HttpServletResponse response, @RequestBody RoomUser roomUser) {
        return RestResponse.generateResponse(spotifyPlayerService.userSyncWithRoom(roomUser), HttpStatus.OK, request, response);
    }
}
