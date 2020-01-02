package vip.yazilim.p2g.web.controller.rest.p2g;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vip.yazilim.p2g.web.config.annotation.HasRoomPrivilege;
import vip.yazilim.p2g.web.config.annotation.HasSystemRole;
import vip.yazilim.p2g.web.constant.Privilege;
import vip.yazilim.p2g.web.constant.Role;
import vip.yazilim.p2g.web.controller.websocket.UserWebSocketController;
import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.entity.RoomInvite;
import vip.yazilim.p2g.web.entity.RoomUser;
import vip.yazilim.p2g.web.model.RoomModel;
import vip.yazilim.p2g.web.service.p2g.IRoomInviteService;
import vip.yazilim.p2g.web.service.p2g.IRoomService;
import vip.yazilim.p2g.web.service.p2g.IRoomUserService;
import vip.yazilim.spring.core.exception.general.InvalidArgumentException;
import vip.yazilim.spring.core.exception.general.InvalidUpdateException;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;
import vip.yazilim.spring.core.rest.ARestCrud;
import vip.yazilim.spring.core.rest.model.RestResponse;
import vip.yazilim.spring.core.service.ICrudService;
import vip.yazilim.spring.core.util.RestResponseFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static vip.yazilim.p2g.web.constant.Constants.API_P2G;

/**
 * @author mustafaarifsisman - 5.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@RestController
@RequestMapping(API_P2G + "/room")
public class RoomRest extends ARestCrud<Room, String> {

    @Autowired
    private IRoomService roomService;

    @Autowired
    private IRoomUserService roomUserService;

    @Autowired
    private IRoomInviteService roomInviteService;

    @Autowired
    private UserWebSocketController userWebSocketController;

    @Override
    protected ICrudService<Room, String> getService() {
        return roomService;
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
    public RestResponse<Room> getById(HttpServletRequest request, HttpServletResponse response, @PathVariable String id) {
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

    @Override
    @HasRoomPrivilege(privilege = Privilege.ROOM_DELETE)
    @DeleteMapping({"/{id}"})
    public RestResponse<Boolean> delete(HttpServletRequest request, HttpServletResponse response, @PathVariable String id) {
        return super.delete(request, response, id);
    }

    ///////////////////////////////
    // Custom controllers
    ///////////////////////////////

    @HasSystemRole(role = Role.P2G_USER)
    @GetMapping("/{roomUuid}/model")
    public RestResponse<RoomModel> getRoomModel(HttpServletRequest request, HttpServletResponse response, @PathVariable String roomUuid) throws DatabaseException, InvalidArgumentException {
        return RestResponseFactory.generateResponse(roomService.getRoomModelByRoomUuid(roomUuid), HttpStatus.OK, request, response);
    }

    // RoomInvite (Invite & Accept & Reject)
    @HasRoomPrivilege(privilege = Privilege.ROOM_INVITE)
    @PostMapping("/{roomUuid}/invite/{userUuid}")
    public RestResponse<RoomInvite> invite(HttpServletRequest request, HttpServletResponse response, @PathVariable String roomUuid, @PathVariable String userUuid) throws DatabaseException, InvalidArgumentException {
        RoomInvite roomInvite = roomInviteService.invite(roomUuid, userUuid);
        userWebSocketController.sendRoomInvite(roomInvite);
        return RestResponseFactory.generateResponse(roomInvite, HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @PostMapping("/invite/accept")
    public RestResponse<RoomUser> accept(HttpServletRequest request, HttpServletResponse response, @RequestBody RoomInvite roomInvite) throws InvalidUpdateException, DatabaseException, InvalidArgumentException {
        return RestResponseFactory.generateResponse(roomInviteService.accept(roomInvite), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @DeleteMapping("/invite/{roomInviteUuid}/reject")
    public RestResponse<Boolean> reject(HttpServletRequest request, HttpServletResponse response, @PathVariable String roomInviteUuid) throws DatabaseException, InvalidArgumentException {
        return RestResponseFactory.generateResponse(roomInviteService.reject(roomInviteUuid), HttpStatus.OK, request, response);
    }

    // RoomUser (Join & Leave & Get Users)
    @HasSystemRole(role = Role.P2G_USER)
    @PostMapping("/{roomUuid}/join")
    public RestResponse<RoomUser> joinRoom(HttpServletRequest request, HttpServletResponse response, @PathVariable String roomUuid, @RequestBody String password) throws DatabaseException, InvalidArgumentException, IOException, SpotifyWebApiException {
        return RestResponseFactory.generateResponse(roomUserService.joinRoom(roomUuid, password, Role.ROOM_USER), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @DeleteMapping("/leave")
    public RestResponse<Boolean> leaveRoom(HttpServletRequest request, HttpServletResponse response) throws DatabaseException, InvalidArgumentException {
        return RestResponseFactory.generateResponse(roomUserService.leaveRoom(), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @GetMapping("/{roomUuid}/users")
    public RestResponse<List<RoomUser>> getRoomUsers(HttpServletRequest request, HttpServletResponse response, @PathVariable String roomUuid) throws DatabaseException {
        return RestResponseFactory.generateResponse(roomUserService.getRoomUsersByRoomUuid(roomUuid), HttpStatus.OK, request, response);
    }

    // Authorities (Promote & Demote)
    @HasRoomPrivilege(privilege = Privilege.ROOM_MANAGE_ROLES)
    @PutMapping("/user/{roomUserUuid}/promote")
    public RestResponse<List<Privilege>> promote(HttpServletRequest request, HttpServletResponse response, @PathVariable String roomUserUuid) throws InvalidUpdateException, DatabaseException, InvalidArgumentException {
        return RestResponseFactory.generateResponse(roomUserService.promoteUserRole(roomUserUuid), HttpStatus.OK, request, response);
    }

    @HasRoomPrivilege(privilege = Privilege.ROOM_MANAGE_ROLES)
    @PutMapping("/user/{roomUserUuid}/demote")
    public RestResponse<List<Privilege>> demote(HttpServletRequest request, HttpServletResponse response, @PathVariable String roomUserUuid) throws InvalidUpdateException, DatabaseException, InvalidArgumentException {
        return RestResponseFactory.generateResponse(roomUserService.demoteUserRole(roomUserUuid), HttpStatus.OK, request, response);
    }
}
