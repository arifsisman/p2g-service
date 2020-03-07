package vip.yazilim.p2g.web.controller.rest.p2g;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
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
import vip.yazilim.spring.core.exception.GeneralException;
import vip.yazilim.spring.core.exception.InvalidArgumentException;
import vip.yazilim.spring.core.exception.database.DatabaseException;
import vip.yazilim.spring.core.rest.ARestCrud;
import vip.yazilim.spring.core.rest.model.RestResponse;
import vip.yazilim.spring.core.rest.model.RestResponseFactory;
import vip.yazilim.spring.core.service.ICrudService;

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
public class RoomRest extends ARestCrud<Room, Long> {

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
    public RestResponse<Boolean> delete(HttpServletRequest request, HttpServletResponse response, @PathVariable Long id) throws DatabaseException {
        return RestResponseFactory.generateResponse(roomService.deleteById(id), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @PostMapping({"/create/{roomName}"})
    public RestResponse<Room> createRoom(HttpServletRequest request, HttpServletResponse response, @PathVariable String roomName, @RequestBody String roomPassword) throws GeneralException {
        return RestResponseFactory.generateResponse(roomService.createRoom(SecurityHelper.getUserId(), roomName, roomPassword), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @GetMapping("/model/{roomId}")
    public RestResponse<RoomModel> getRoomModel(HttpServletRequest request, HttpServletResponse response, @PathVariable Long roomId) throws DatabaseException, InvalidArgumentException {
        return RestResponseFactory.generateResponse(roomService.getRoomModelByRoomId(roomId), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @GetMapping("/model/me")
    public RestResponse<RoomModel> getRoomModelByUser(HttpServletRequest request, HttpServletResponse response) throws DatabaseException, InvalidArgumentException {
        return RestResponseFactory.generateResponse(roomService.getRoomModelByUserId(SecurityHelper.getUserId()), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @GetMapping("/smodel/{roomId}")
    public RestResponse<RoomModelSimplified> getRoomModelSimplified(HttpServletRequest request, HttpServletResponse response, @PathVariable Long roomId) throws DatabaseException, InvalidArgumentException {
        return RestResponseFactory.generateResponse(roomService.getRoomModelSimplifiedByRoomId(roomId), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @GetMapping("/model/")
    public RestResponse<List<RoomModelSimplified>> getSimplifiedRoomModels(HttpServletRequest request, HttpServletResponse response) throws DatabaseException, InvalidArgumentException {
        return RestResponseFactory.generateResponse(roomService.getSimplifiedRoomModels(), HttpStatus.OK, request, response);
    }

    // RoomInvite (Invite & Accept & Reject)
    @HasRoomPrivilege(privilege = Privilege.ROOM_INVITE)
    @PostMapping("/{roomId}/invite/{userId}")
    public RestResponse<RoomInvite> invite(HttpServletRequest request, HttpServletResponse response, @PathVariable Long roomId, @PathVariable String userId) throws GeneralException {
        return RestResponseFactory.generateResponse(roomInviteService.invite(roomId, userId), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @UpdateRoomUsers
    @PostMapping("/invite/accept")
    public RestResponse<RoomUser> accept(HttpServletRequest request, HttpServletResponse response, @RequestBody RoomInvite roomInvite) throws GeneralException {
        return RestResponseFactory.generateResponse(roomInviteService.accept(roomInvite), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @DeleteMapping("/invite/{roomInviteId}/reject")
    public RestResponse<Boolean> reject(HttpServletRequest request, HttpServletResponse response, @PathVariable Long roomInviteId) throws DatabaseException {
        return RestResponseFactory.generateResponse(roomInviteService.reject(roomInviteId), HttpStatus.OK, request, response);
    }

    // RoomUser (Join & Leave & Get Users)
    @HasSystemRole(role = Role.P2G_USER)
    @UpdateRoomUsers
    @PostMapping("/{roomId}/join")
    public RestResponse<RoomUser> joinRoom(HttpServletRequest request, HttpServletResponse response, @PathVariable Long roomId, @RequestBody String password) throws GeneralException, IOException, SpotifyWebApiException {
        return RestResponseFactory.generateResponse(roomUserService.joinRoom(roomId, SecurityHelper.getUserId(), password, Role.ROOM_USER), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @DeleteMapping("/leave")
    public RestResponse<Boolean> leaveRoom(HttpServletRequest request, HttpServletResponse response) throws DatabaseException {
        return RestResponseFactory.generateResponse(roomUserService.leaveRoom(), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @GetMapping("/{roomId}/users")
    public RestResponse<List<RoomUser>> getRoomUsers(HttpServletRequest request, HttpServletResponse response, @PathVariable Long roomId) throws DatabaseException {
        return RestResponseFactory.generateResponse(roomUserService.getRoomUsersByRoomId(roomId), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @GetMapping("/{roomId}/roomUserModels")
    public RestResponse<List<RoomUserModel>> getRoomUserModels(HttpServletRequest request, HttpServletResponse response, @PathVariable Long roomId) throws DatabaseException, InvalidArgumentException {
        return RestResponseFactory.generateResponse(roomUserService.getRoomUserModelsByRoomId(roomId), HttpStatus.OK, request, response);
    }


    // Authorities (Promote & Demote)
    @HasRoomPrivilege(privilege = Privilege.ROOM_MANAGE_ROLES)
    @UpdateRoomUsers
    @PutMapping("/user/{roomUserId}/promote")
    public RestResponse<RoomUser> promote(HttpServletRequest request, HttpServletResponse response, @PathVariable Long roomUserId) throws DatabaseException, InvalidArgumentException {
        return RestResponseFactory.generateResponse(roomUserService.promoteUserRole(roomUserId), HttpStatus.OK, request, response);
    }

    @HasRoomPrivilege(privilege = Privilege.ROOM_MANAGE_ROLES)
    @UpdateRoomUsers
    @PutMapping("/user/{roomUserId}/demote")
    public RestResponse<RoomUser> demote(HttpServletRequest request, HttpServletResponse response, @PathVariable Long roomUserId) throws DatabaseException, InvalidArgumentException {
        return RestResponseFactory.generateResponse(roomUserService.demoteUserRole(roomUserId), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @GetMapping({"/invite/model"})
    public RestResponse<List<RoomInviteModel>> getRoomInviteModel(HttpServletRequest request, HttpServletResponse response) throws DatabaseException, InvalidArgumentException {
        return RestResponseFactory.generateResponse(roomInviteService.getRoomInviteModelListByUserId(SecurityHelper.getUserId()), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @GetMapping({"/user/me"})
    public RestResponse<RoomUser> getRoomUserMe(HttpServletRequest request, HttpServletResponse response) throws DatabaseException {
        return RestResponseFactory.generateResponse(roomUserService.getRoomUserMe(SecurityHelper.getUserId()), HttpStatus.OK, request, response);
    }

    // Clear song list
    @HasRoomPrivilege(privilege = Privilege.ROOM_CLEAR_QUEUE)
    @UpdateRoomSongs
    @PostMapping("/{roomId}/queue/clear")
    public RestResponse<Boolean> clearSongList(HttpServletRequest request, HttpServletResponse response, @PathVariable Long roomId) throws DatabaseException, SpotifyWebApiException, IOException, InvalidArgumentException {
        return RestResponseFactory.generateResponse(songService.clearRoomSongList(roomId), HttpStatus.OK, request, response);
    }

}
