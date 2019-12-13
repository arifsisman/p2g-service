package vip.yazilim.p2g.web.controller.rest.p2g;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.entity.relation.RoomInvite;
import vip.yazilim.p2g.web.entity.relation.RoomUser;
import vip.yazilim.p2g.web.model.RoomModel;
import vip.yazilim.p2g.web.service.p2g.IRoomService;
import vip.yazilim.p2g.web.service.p2g.relation.IRoomInviteService;
import vip.yazilim.p2g.web.service.p2g.relation.IRoomUserService;
import vip.yazilim.spring.core.exception.web.NotFoundException;
import vip.yazilim.spring.core.exception.web.ServiceException;
import vip.yazilim.spring.core.rest.ARestCrud;
import vip.yazilim.spring.core.rest.model.RestResponse;
import vip.yazilim.spring.core.service.ICrudService;
import vip.yazilim.spring.core.util.RestResponseFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Optional;

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

    @Override
    protected ICrudService<Room, String> getService() {
        return roomService;
    }

    // Room
    @PreAuthorize(value = "hasAuthority('join_room')")
    @GetMapping("/{roomUuid}/model")
    public RestResponse<RoomModel> getRoomModel(HttpServletRequest request, HttpServletResponse response, @PathVariable String roomUuid) {
        Optional<RoomModel> roomModel;

        try {
            roomModel = roomService.getRoomModelByRoomUuid(roomUuid);
        } catch (Exception e) {
            throw new ServiceException(e);
        }

        if (!roomModel.isPresent()) {
            throw new NotFoundException("Model Not Found");
        } else {
            return RestResponseFactory.generateResponse(roomModel.get(), HttpStatus.OK, request, response);
        }
    }

    @Override
    @DeleteMapping("/{roomUuid}")
    public RestResponse<Boolean> delete(HttpServletRequest request, HttpServletResponse response, @PathVariable String roomUuid) {
        boolean status;

        try {
            status = roomService.deleteRoom(roomUuid);
        } catch (Exception e) {
            throw new ServiceException(e);
        }

        return RestResponseFactory.generateResponse(status, HttpStatus.OK, request, response);
    }

    // RoomInvite
    @PostMapping("/{roomUuid}/invite/{userUuid}")
    public RestResponse<RoomInvite> invite(HttpServletRequest request, HttpServletResponse response, @PathVariable String roomUuid, @PathVariable String userUuid) {
        RoomInvite roomInvite;

        try {
            roomInvite = roomInviteService.invite(roomUuid, userUuid);
        } catch (Exception e) {
            throw new ServiceException(e);
        }

        return RestResponseFactory.generateResponse(roomInvite, HttpStatus.OK, request, response);
    }

    @PostMapping("/invite/accept")
    public RestResponse<RoomUser> accept(HttpServletRequest request, HttpServletResponse response, @RequestBody RoomInvite roomInvite) {
        RoomUser roomUser;

        try {
            roomUser = roomInviteService.accept(roomInvite);
        } catch (Exception e) {
            throw new ServiceException(e);
        }

        return RestResponseFactory.generateResponse(roomUser, HttpStatus.OK, request, response);
    }

    @DeleteMapping("/invite/{roomInviteUuid}/reject")
    public RestResponse<Boolean> reject(HttpServletRequest request, HttpServletResponse response, @PathVariable String roomInviteUuid) {
        boolean status;

        try {
            status = roomInviteService.reject(roomInviteUuid);
        } catch (Exception e) {
            throw new ServiceException(e);
        }

        return RestResponseFactory.generateResponse(status, HttpStatus.OK, request, response);
    }

    // RoomUser
    @PostMapping("/{roomUuid}/join")
    public RestResponse<RoomUser> joinRoom(HttpServletRequest request, HttpServletResponse response, @PathVariable String roomUuid, @RequestBody Map<String, String> userUuidAndPassword) {
        RoomUser roomUser;

        try {
            roomUser = roomUserService.joinRoom(roomUuid, userUuidAndPassword.get("userUuid"), userUuidAndPassword.get("roomPassword"));
        } catch (Exception e) {
            throw new ServiceException(e);
        }

        return RestResponseFactory.generateResponse(roomUser, HttpStatus.OK, request, response);
    }

    @DeleteMapping("/{roomUserUuid}/leave")
    public RestResponse<Boolean> leaveRoom(HttpServletRequest request, HttpServletResponse response, @PathVariable String roomUserUuid) {
        boolean status;

        try {
            status = roomUserService.deleteById(roomUserUuid);
        } catch (Exception e) {
            throw new ServiceException(e);
        }

        return RestResponseFactory.generateResponse(status, HttpStatus.OK, request, response);
    }

}
