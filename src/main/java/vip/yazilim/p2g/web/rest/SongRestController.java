package vip.yazilim.p2g.web.rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vip.yazilim.libs.springcore.rest.model.RestResponse;
import vip.yazilim.p2g.web.config.annotation.HasRoomPrivilege;
import vip.yazilim.p2g.web.config.annotation.UpdateRoomSongs;
import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.p2g.web.enums.Privilege;
import vip.yazilim.p2g.web.model.SearchModel;
import vip.yazilim.p2g.web.service.p2g.ISongService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static vip.yazilim.p2g.web.constant.Constants.API_P2G;

/**
 * @author mustafaarifsisman - 29.06.2020
 * @contact mustafaarifsisman@gmail.com
 */
@RestController
@RequestMapping(API_P2G + "/room")
public class SongRestController {

    private final ISongService songService;

    public SongRestController(ISongService songService) {
        this.songService = songService;
    }

    @HasRoomPrivilege(privilege = Privilege.SONG_GET)
    @GetMapping("/{roomId}/queue")
    public RestResponse<List<Song>> getSongListByRoomId(HttpServletRequest request, HttpServletResponse response, @PathVariable Long roomId) {
        return RestResponse.generateResponse(songService.getSongListByRoomId(roomId, false), HttpStatus.OK, request, response);
    }

    @HasRoomPrivilege(privilege = Privilege.SONG_ADD_AND_REMOVE)
    @UpdateRoomSongs
    @PostMapping("/{roomId}/queue")
    public RestResponse<List<Song>> addSongWithSearchModels(HttpServletRequest request, HttpServletResponse response, @PathVariable Long roomId, @RequestBody SearchModel searchModel) {
        return RestResponse.generateResponse(songService.addSongWithSearchModel(roomId, searchModel), HttpStatus.OK, request, response);
    }

    @HasRoomPrivilege(privilege = Privilege.ROOM_CLEAR_QUEUE)
    @UpdateRoomSongs
    @DeleteMapping("/{roomId}/queue")
    public RestResponse<Boolean> clearSongList(HttpServletRequest request, HttpServletResponse response, @PathVariable Long roomId) {
        return RestResponse.generateResponse(songService.deleteRoomSongList(roomId), HttpStatus.OK, request, response);
    }

    @HasRoomPrivilege(privilege = Privilege.SONG_ADD_AND_REMOVE)
    @UpdateRoomSongs
    @DeleteMapping({"/queue/{songId}/remove"})
    public RestResponse<Boolean> removeSongFromRoom(HttpServletRequest request, HttpServletResponse response, @PathVariable Long songId) {
        return RestResponse.generateResponse(songService.removeSongFromRoom(songId), HttpStatus.OK, request, response);
    }

    @HasRoomPrivilege(privilege = Privilege.SONG_VOTE)
    @UpdateRoomSongs
    @PutMapping("/queue/{songId}/upvote")
    public RestResponse<Integer> upvote(HttpServletRequest request, HttpServletResponse response, @PathVariable Long songId) {
        return RestResponse.generateResponse(songService.upvote(songId), HttpStatus.OK, request, response);
    }

    @HasRoomPrivilege(privilege = Privilege.SONG_VOTE)
    @UpdateRoomSongs
    @PutMapping("/queue/{songId}/downvote")
    public RestResponse<Integer> downvote(HttpServletRequest request, HttpServletResponse response, @PathVariable Long songId) {
        return RestResponse.generateResponse(songService.downvote(songId), HttpStatus.OK, request, response);
    }

}
