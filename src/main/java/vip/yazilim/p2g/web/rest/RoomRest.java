package vip.yazilim.p2g.web.rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vip.yazilim.libs.springcore.rest.model.RestResponse;
import vip.yazilim.p2g.web.config.annotation.HasRoomPrivilege;
import vip.yazilim.p2g.web.config.annotation.HasSystemRole;
import vip.yazilim.p2g.web.config.annotation.UpdateRoomSongs;
import vip.yazilim.p2g.web.config.annotation.UpdateRoomUsers;
import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.entity.RoomInvite;
import vip.yazilim.p2g.web.entity.RoomUser;
import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.p2g.web.enums.Privilege;
import vip.yazilim.p2g.web.enums.Role;
import vip.yazilim.p2g.web.model.RoomInviteModel;
import vip.yazilim.p2g.web.model.RoomModel;
import vip.yazilim.p2g.web.model.RoomUserModel;
import vip.yazilim.p2g.web.model.SearchModel;
import vip.yazilim.p2g.web.service.p2g.IRoomInviteService;
import vip.yazilim.p2g.web.service.p2g.IRoomService;
import vip.yazilim.p2g.web.service.p2g.IRoomUserService;
import vip.yazilim.p2g.web.service.p2g.ISongService;
import vip.yazilim.p2g.web.util.SecurityHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

import static vip.yazilim.p2g.web.constant.Constants.API_P2G;

/**
 * @author mustafaarifsisman - 5.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@RestController
@RequestMapping(API_P2G + "/room")
public class RoomRest {

    private final IRoomService roomService;
    private final IRoomUserService roomUserService;
    private final ISongService songService;
    private final IRoomInviteService roomInviteService;

    public RoomRest(IRoomService roomService, IRoomUserService roomUserService, ISongService songService, IRoomInviteService roomInviteService) {
        this.roomService = roomService;
        this.roomUserService = roomUserService;
        this.songService = songService;
        this.roomInviteService = roomInviteService;
    }

    @HasSystemRole(role = Role.P2G_USER)
    @PostMapping({"/create/{roomName}"})
    public RestResponse<RoomUserModel> createRoom(HttpServletRequest request, HttpServletResponse response, @PathVariable String roomName, @RequestBody(required = false) String roomPassword) {
        return RestResponse.generateResponse(roomService.createRoom(SecurityHelper.getUserId(), roomName, roomPassword), HttpStatus.OK, request, response);
    }

    @HasRoomPrivilege(privilege = Privilege.ROOM_UPDATE)
    @PutMapping({"/"})
    public RestResponse<Room> update(HttpServletRequest request, HttpServletResponse response, @RequestBody Room room) {
        return RestResponse.generateResponse(roomService.update(room), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @GetMapping("/model/{roomId}")
    public RestResponse<Optional<RoomModel>> getRoomModel(HttpServletRequest request, HttpServletResponse response, @PathVariable Long roomId) {
        return RestResponse.generateResponse(roomService.getRoomModelByRoomId(roomId), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @GetMapping("/model/me")
    public RestResponse<Optional<RoomModel>> getRoomModelMe(HttpServletRequest request, HttpServletResponse response) {
        return RestResponse.generateResponse(roomService.getRoomModelByUserId(SecurityHelper.getUserId()), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @GetMapping("/model/")
    public RestResponse<List<RoomModel>> getRoomModels(HttpServletRequest request, HttpServletResponse response) {
        return RestResponse.generateResponse(roomService.getRoomModels(), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @UpdateRoomUsers
    @PostMapping("/{roomId}/join")
    public RestResponse<RoomUserModel> joinRoom(HttpServletRequest request, HttpServletResponse response, @PathVariable Long roomId, @RequestBody String password) {
        return RestResponse.generateResponse(roomUserService.joinRoom(roomId, SecurityHelper.getUserId(), password, Role.ROOM_USER), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @GetMapping("/{roomId}/users")
    public RestResponse<List<RoomUser>> getRoomUsers(HttpServletRequest request, HttpServletResponse response, @PathVariable Long roomId) {
        return RestResponse.generateResponse(roomUserService.getRoomUsersByRoomId(roomId), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @GetMapping("/{roomId}/roomUserModels")
    public RestResponse<List<RoomUserModel>> getRoomUserModels(HttpServletRequest request, HttpServletResponse response, @PathVariable Long roomId) {
        return RestResponse.generateResponse(roomUserService.getRoomUserModelsByRoomId(roomId), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @DeleteMapping("/user/leave")
    public RestResponse<Boolean> leaveRoom(HttpServletRequest request, HttpServletResponse response) {
        return RestResponse.generateResponse(roomUserService.leaveRoom(), HttpStatus.OK, request, response);
    }

    @HasRoomPrivilege(privilege = Privilege.ROOM_MANAGE_ROLES)
    @UpdateRoomUsers
    @PutMapping("/user/{roomUserId}/changeRole")
    public RestResponse<RoomUser> changeRoomUserRole(HttpServletRequest request, HttpServletResponse response, @PathVariable Long roomUserId, @RequestBody String roleName) {
        return RestResponse.generateResponse(roomUserService.changeRoomUserRole(roomUserId, roleName), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @GetMapping({"/user/me"})
    public RestResponse<RoomUserModel> getRoomUserModelMe(HttpServletRequest request, HttpServletResponse response) {
        return RestResponse.generateResponse(roomUserService.getRoomUserModelMe(SecurityHelper.getUserId()), HttpStatus.OK, request, response);
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

    @HasRoomPrivilege(privilege = Privilege.ROOM_INVITE_AND_REPLY)
    @PostMapping("/{roomId}/invite/{userId}")
    public RestResponse<RoomInvite> invite(HttpServletRequest request, HttpServletResponse response, @PathVariable Long roomId, @PathVariable String userId) {
        return RestResponse.generateResponse(roomInviteService.invite(roomId, userId), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @UpdateRoomUsers
    @PostMapping("/invite/accept")
    public RestResponse<RoomUserModel> accept(HttpServletRequest request, HttpServletResponse response, @RequestBody RoomInvite roomInvite) {
        return RestResponse.generateResponse(roomInviteService.accept(roomInvite), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @DeleteMapping("/invite/{roomInviteId}")
    public RestResponse<Boolean> reject(HttpServletRequest request, HttpServletResponse response, @PathVariable Long roomInviteId) {
        return RestResponse.generateResponse(roomInviteService.reject(roomInviteId), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @GetMapping({"/invite/model"})
    public RestResponse<List<RoomInviteModel>> getRoomInviteModel(HttpServletRequest request, HttpServletResponse response) {
        return RestResponse.generateResponse(roomInviteService.getRoomInviteModelListByUserId(SecurityHelper.getUserId()), HttpStatus.OK, request, response);
    }
}
