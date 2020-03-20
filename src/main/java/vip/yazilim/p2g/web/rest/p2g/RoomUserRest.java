package vip.yazilim.p2g.web.rest.p2g;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vip.yazilim.libs.springcore.rest.model.RestResponse;
import vip.yazilim.p2g.web.config.annotation.HasRoomPrivilege;
import vip.yazilim.p2g.web.config.annotation.HasRoomRole;
import vip.yazilim.p2g.web.config.annotation.HasSystemRole;
import vip.yazilim.p2g.web.config.annotation.UpdateRoomUsers;
import vip.yazilim.p2g.web.entity.RoomUser;
import vip.yazilim.p2g.web.enums.Privilege;
import vip.yazilim.p2g.web.enums.Role;
import vip.yazilim.p2g.web.model.RoomUserModel;
import vip.yazilim.p2g.web.service.p2g.IRoomUserService;
import vip.yazilim.p2g.web.util.SecurityHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static vip.yazilim.p2g.web.constant.Constants.API_P2G;

/**
 * @author mustafaarifsisman - 20.03.2020
 * @contact mustafaarifsisman@gmail.com
 */
@RestController
@RequestMapping(API_P2G + "/room")
public class RoomUserRest {

    @Autowired
    private IRoomUserService roomUserService;

    @HasSystemRole(role = Role.P2G_USER)
    @UpdateRoomUsers
    @PostMapping("/{roomId}/join")
    public RestResponse<RoomUser> joinRoom(HttpServletRequest request, HttpServletResponse response, @PathVariable Long roomId, @RequestBody String password) {
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

    @HasRoomRole(role = Role.ROOM_OWNER)
    @UpdateRoomUsers
    @PutMapping("/user/{roomUserId}/makeOwner")
    public RestResponse<Boolean> changeRoomOwner(HttpServletRequest request, HttpServletResponse response, @PathVariable Long roomUserId) {
        return RestResponse.generateResponse(roomUserService.changeRoomOwner(roomUserId), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @GetMapping({"/user/me"})
    public RestResponse<RoomUserModel> getRoomUserModelMe(HttpServletRequest request, HttpServletResponse response) {
        return RestResponse.generateResponse(roomUserService.getRoomUserModelMe(SecurityHelper.getUserId()), HttpStatus.OK, request, response);
    }
}
