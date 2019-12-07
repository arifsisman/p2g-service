package vip.yazilim.p2g.web.controller.rest.p2g;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.model.RoomModel;
import vip.yazilim.p2g.web.service.p2g.IRoomService;
import vip.yazilim.p2g.web.service.p2g.relation.IRoomInviteService;
import vip.yazilim.p2g.web.service.p2g.relation.IRoomQueueService;
import vip.yazilim.p2g.web.service.p2g.relation.IRoomUserService;
import vip.yazilim.spring.core.exception.web.NotFoundException;
import vip.yazilim.spring.core.exception.web.ServiceException;
import vip.yazilim.spring.core.rest.ARestCrud;
import vip.yazilim.spring.core.rest.model.RestErrorResponse;
import vip.yazilim.spring.core.rest.model.RestResponse;
import vip.yazilim.spring.core.rest.model.RestResponseFactory;
import vip.yazilim.spring.core.service.ICrudService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    private IRoomQueueService roomQueueService;

    @Autowired
    private IRoomUserService roomUserService;

    @Autowired
    private IRoomInviteService roomInviteService;

    @Override
    protected ICrudService<Room, String> getService() {
        return roomService;
    }

    @GetMapping("/{roomUuid}/model")
    @CrossOrigin(origins = {"*"})
    @ApiResponses({@ApiResponse(code = 404, message = "Model not found", response = RestErrorResponse.class), @ApiResponse(code = 500, message = "Internal Error", response = RestErrorResponse.class)})
    public RestResponse<RoomModel> getRoomModel(HttpServletRequest request, HttpServletResponse response, @PathVariable String roomUuid) {
        Optional<RoomModel> roomModel;

        try {
            roomModel = roomService.getRoomModelByRoomUuid(roomUuid);
        } catch (Exception var7) {
            throw new ServiceException(var7);
        }

        if (!roomModel.isPresent()) {
            throw new NotFoundException("Model Not Found");
        } else {
            return RestResponseFactory.generateResponse(roomModel.get(), HttpStatus.OK, request, response);
        }
    }

    @DeleteMapping("/{roomUuid}")
    @CrossOrigin(origins = {"*"})
    @ApiResponses({@ApiResponse(code = 404, message = "Entity not found", response = RestErrorResponse.class), @ApiResponse(code = 500, message = "Internal Error", response = RestErrorResponse.class)})
    public RestResponse<Boolean> deleteRoom(HttpServletRequest request, HttpServletResponse response, @PathVariable String roomUuid) {
        boolean status;

        try {
            //delete room
            status = roomService.deleteRoom(roomUuid);

            //delete roomQueues
            roomQueueService.deleteRoomSongList(roomUuid);

            //delete roomUsers
            roomUserService.deleteRoomUsers(roomUuid);

            //delete roomInvites
            roomInviteService.deleteRoomInvites(roomUuid);
        } catch (Exception var7) {
            throw new ServiceException(var7);
        }

        return RestResponseFactory.generateResponse(status, HttpStatus.OK, request, response);
    }
}
