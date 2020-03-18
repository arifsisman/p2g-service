package vip.yazilim.p2g.web.controller.rest.p2g;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vip.yazilim.libs.springcore.rest.ARestCru;
import vip.yazilim.libs.springcore.rest.model.RestResponse;
import vip.yazilim.libs.springcore.service.ICrudService;
import vip.yazilim.p2g.web.config.annotation.HasRoomPrivilege;
import vip.yazilim.p2g.web.config.annotation.HasSystemRole;
import vip.yazilim.p2g.web.config.annotation.UpdateRoomSongs;
import vip.yazilim.p2g.web.config.annotation.UpdateRoomUsers;
import vip.yazilim.p2g.web.constant.enums.Privilege;
import vip.yazilim.p2g.web.constant.enums.Role;
import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.entity.RoomInvite;
import vip.yazilim.p2g.web.entity.RoomUser;
import vip.yazilim.p2g.web.model.RoomInviteModel;
import vip.yazilim.p2g.web.model.RoomModel;
import vip.yazilim.p2g.web.model.RoomModelSimplified;
import vip.yazilim.p2g.web.model.RoomUserModel;
import vip.yazilim.p2g.web.service.p2g.IRoomInviteService;
import vip.yazilim.p2g.web.service.p2g.IRoomService;
import vip.yazilim.p2g.web.service.p2g.IRoomUserService;
import vip.yazilim.p2g.web.service.p2g.ISongService;
import vip.yazilim.p2g.web.util.SecurityHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static vip.yazilim.p2g.web.constant.Constants.API_P2G;

/**
 * @author mustafaarifsisman - 5.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@RestController
@RequestMapping(API_P2G + "/room")
public class RoomRest extends ARestCru<Room, Long> {

    @Autowired
    private IRoomService roomService;

    @Autowired
    private IRoomUserService roomUserService;

    @Autowired
    private IRoomInviteService roomInviteService;

    @Autowired
    private ISongService songService;

    @Override
    protected ICrudService<Room, Long> getService() {
        return roomService;
    }

    @Override
    protected Class<Room> getClassOfEntity() {
        return Room.class;
    }

    ///////////////////////////////
    // Super class CRUD controllers
    ///////////////////////////////

    @Override
    @HasSystemRole(role = Role.P2G_USER)
    @PostMapping({"/"})
    public RestResponse<Room> create(HttpServletRequest request, HttpServletResponse response, @RequestBody Room entity) {
        return super.create(request, response, entity);
    }

    @Override
    @HasSystemRole(role = Role.P2G_USER)
    @GetMapping({"/{id}"})
    public RestResponse<Room> getById(HttpServletRequest request, HttpServletResponse response, @PathVariable Long id) {
        return super.getById(request, response, id);
    }

    @Override
    @HasSystemRole(role = Role.P2G_USER)
    @GetMapping({"/"})
    public RestResponse<List<Room>> getAll(HttpServletRequest request, HttpServletResponse response) {
        return super.getAll(request, response);
    }

    @Override
    @HasRoomPrivilege(privilege = Privilege.ROOM_UPDATE)
    @PutMapping({"/"})
    public RestResponse<Room> update(HttpServletRequest request, HttpServletResponse response, @RequestBody Room entity) {
        return super.update(request, response, entity);
    }

    ///////////////////////////////
    // Custom controllers
    ///////////////////////////////

    @HasRoomPrivilege(privilege = Privilege.ROOM_DELETE)
    @DeleteMapping({"/{id}"})
    public RestResponse<Boolean> delete(HttpServletRequest request, HttpServletResponse response, @PathVariable Long id) {
        return RestResponse.generateResponse(roomService.deleteById(id), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @PostMapping({"/create/{roomName}"})
    public RestResponse<Room> createRoom(HttpServletRequest request, HttpServletResponse response, @PathVariable String roomName, @RequestBody(required = false) String roomPassword) {
        return RestResponse.generateResponse(roomService.createRoom(SecurityHelper.getUserId(), roomName, roomPassword), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @GetMapping("/model/{roomId}")
    public RestResponse<RoomModel> getRoomModel(HttpServletRequest request, HttpServletResponse response, @PathVariable Long roomId) {
        return RestResponse.generateResponse(roomService.getRoomModelByRoomId(roomId), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @GetMapping("/model/me")
    public RestResponse<RoomModel> getRoomModelByUser(HttpServletRequest request, HttpServletResponse response) {
        return RestResponse.generateResponse(roomService.getRoomModelByUserId(SecurityHelper.getUserId()), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @GetMapping("/smodel/{roomId}")
    public RestResponse<RoomModelSimplified> getRoomModelSimplified(HttpServletRequest request, HttpServletResponse response, @PathVariable Long roomId) {
        return RestResponse.generateResponse(roomService.getRoomModelSimplifiedByRoomId(roomId), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @GetMapping("/model/")
    public RestResponse<List<RoomModelSimplified>> getSimplifiedRoomModels(HttpServletRequest request, HttpServletResponse response) {
        return RestResponse.generateResponse(roomService.getSimplifiedRoomModels(), HttpStatus.OK, request, response);
    }

    // RoomInvite (Invite & Accept & Reject)
    @HasRoomPrivilege(privilege = Privilege.ROOM_INVITE_AND_REPLY)
    @PostMapping("/{roomId}/invite/{userId}")
    public RestResponse<RoomInvite> invite(HttpServletRequest request, HttpServletResponse response, @PathVariable Long roomId, @PathVariable String userId) {
        return RestResponse.generateResponse(roomInviteService.invite(roomId, userId), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @UpdateRoomUsers
    @PostMapping("/invite/accept")
    public RestResponse<RoomUser> accept(HttpServletRequest request, HttpServletResponse response, @RequestBody RoomInvite roomInvite) {
        return RestResponse.generateResponse(roomInviteService.accept(roomInvite), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @DeleteMapping("/invite/{roomInviteId}/reject")
    public RestResponse<Boolean> reject(HttpServletRequest request, HttpServletResponse response, @PathVariable Long roomInviteId) {
        return RestResponse.generateResponse(roomInviteService.reject(roomInviteId), HttpStatus.OK, request, response);
    }

    // RoomUser (Join & Leave & Get Users)
    @HasSystemRole(role = Role.P2G_USER)
    @UpdateRoomUsers
    @PostMapping("/{roomId}/join")
    public RestResponse<RoomUser> joinRoom(HttpServletRequest request, HttpServletResponse response, @PathVariable Long roomId, @RequestBody String password) {
        return RestResponse.generateResponse(roomUserService.joinRoom(roomId, SecurityHelper.getUserId(), password, Role.ROOM_USER), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @DeleteMapping("/leave")
    public RestResponse<Boolean> leaveRoom(HttpServletRequest request, HttpServletResponse response) {
        return RestResponse.generateResponse(roomUserService.leaveRoom(), HttpStatus.OK, request, response);
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


    // Authorities (Promote & Demote)
    @HasRoomPrivilege(privilege = Privilege.ROOM_MANAGE_ROLES)
    @UpdateRoomUsers
    @PutMapping("/user/{roomUserId}/promote")
    public RestResponse<RoomUser> promote(HttpServletRequest request, HttpServletResponse response, @PathVariable Long roomUserId) {
        return RestResponse.generateResponse(roomUserService.changeRoomUserRole(roomUserId, true), HttpStatus.OK, request, response);
    }

    @HasRoomPrivilege(privilege = Privilege.ROOM_MANAGE_ROLES)
    @UpdateRoomUsers
    @PutMapping("/user/{roomUserId}/demote")
    public RestResponse<RoomUser> demote(HttpServletRequest request, HttpServletResponse response, @PathVariable Long roomUserId) {
        return RestResponse.generateResponse(roomUserService.changeRoomUserRole(roomUserId, false), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @GetMapping({"/invite/model"})
    public RestResponse<List<RoomInviteModel>> getRoomInviteModel(HttpServletRequest request, HttpServletResponse response) {
        return RestResponse.generateResponse(roomInviteService.getRoomInviteModelListByUserId(SecurityHelper.getUserId()), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @GetMapping({"/user/me"})
    public RestResponse<RoomUserModel> getRoomUserModelMe(HttpServletRequest request, HttpServletResponse response) {
        return RestResponse.generateResponse(roomUserService.getRoomUserModelMe(SecurityHelper.getUserId()), HttpStatus.OK, request, response);
    }

    // Clear song list
    @HasRoomPrivilege(privilege = Privilege.ROOM_CLEAR_QUEUE)
    @UpdateRoomSongs
    @PostMapping("/{roomId}/queue/clear")
    public RestResponse<Boolean> clearSongList(HttpServletRequest request, HttpServletResponse response, @PathVariable Long roomId) {
        return RestResponse.generateResponse(songService.deleteRoomSongList(roomId), HttpStatus.OK, request, response);
    }

}
