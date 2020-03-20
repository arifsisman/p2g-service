package vip.yazilim.p2g.web.rest.p2g;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vip.yazilim.libs.springcore.rest.model.RestResponse;
import vip.yazilim.p2g.web.config.annotation.HasRoomPrivilege;
import vip.yazilim.p2g.web.config.annotation.HasSystemRole;
import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.enums.Privilege;
import vip.yazilim.p2g.web.enums.Role;
import vip.yazilim.p2g.web.model.RoomModel;
import vip.yazilim.p2g.web.service.p2g.IRoomService;
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
public class RoomRest {

    @Autowired
    private IRoomService roomService;

    @HasSystemRole(role = Role.P2G_USER)
    @PostMapping({"/create/{roomName}"})
    public RestResponse<Room> createRoom(HttpServletRequest request, HttpServletResponse response, @PathVariable String roomName, @RequestBody(required = false) String roomPassword) {
        return RestResponse.generateResponse(roomService.createRoom(SecurityHelper.getUserId(), roomName, roomPassword), HttpStatus.OK, request, response);
    }

    @HasRoomPrivilege(privilege = Privilege.ROOM_UPDATE)
    @PutMapping({"/"})
    public RestResponse<Room> update(HttpServletRequest request, HttpServletResponse response, @RequestBody Room room) {
        return RestResponse.generateResponse(roomService.update(room), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @GetMapping("/model/{roomId}")
    public RestResponse<RoomModel> getRoomModel(HttpServletRequest request, HttpServletResponse response, @PathVariable Long roomId) {
        return RestResponse.generateResponse(roomService.getRoomModelByRoomId(roomId), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @GetMapping("/model/me")
    public RestResponse<RoomModel> getRoomModelMe(HttpServletRequest request, HttpServletResponse response) {
        return RestResponse.generateResponse(roomService.getRoomModelByUserId(SecurityHelper.getUserId()), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @GetMapping("/model/")
    public RestResponse<List<RoomModel>> getRoomModels(HttpServletRequest request, HttpServletResponse response) {
        return RestResponse.generateResponse(roomService.getRoomModels(), HttpStatus.OK, request, response);
    }

}
