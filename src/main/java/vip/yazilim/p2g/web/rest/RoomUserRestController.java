package vip.yazilim.p2g.web.rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vip.yazilim.libs.springcore.rest.model.RestResponse;
import vip.yazilim.p2g.web.config.annotation.HasRoomPrivilege;
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
 * @author mustafaarifsisman - 29.06.2020
 * @contact mustafaarifsisman@gmail.com
 */
@RestController
@RequestMapping(API_P2G + "/room")
public class RoomUserRestController {

    private final IRoomUserService roomUserService;

    public RoomUserRestController(IRoomUserService roomUserService) {
        this.roomUserService = roomUserService;
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

}
