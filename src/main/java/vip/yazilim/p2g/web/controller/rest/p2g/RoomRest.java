package vip.yazilim.p2g.web.controller.rest.p2g;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.model.RoomModel;
import vip.yazilim.p2g.web.service.p2g.IRoomService;
import vip.yazilim.spring.core.exception.web.NotFoundException;
import vip.yazilim.spring.core.exception.web.ServiceException;
import vip.yazilim.spring.core.rest.ARestCrud;
import vip.yazilim.spring.core.rest.model.RestErrorResponse;
import vip.yazilim.spring.core.rest.model.RestResponse;
import vip.yazilim.spring.core.service.ICrudService;
import vip.yazilim.spring.core.util.RestResponseFactory;

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

    @Override
    protected ICrudService<Room, String> getService() {
        return roomService;
    }

    @PreAuthorize(value = "hasAuthority('join_room')")
    @GetMapping("/{roomUuid}/model")
    @CrossOrigin(origins = {"*"})
    @ApiResponses({@ApiResponse(code = 404, message = "Model not found", response = RestErrorResponse.class), @ApiResponse(code = 500, message = "Internal Error", response = RestErrorResponse.class)})
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
    @CrossOrigin(origins = {"*"})
    @ApiResponses({@ApiResponse(code = 404, message = "Entity not found", response = RestErrorResponse.class), @ApiResponse(code = 500, message = "Internal Error", response = RestErrorResponse.class)})
    public RestResponse<Boolean> delete(HttpServletRequest request, HttpServletResponse response, @PathVariable String roomUuid) {
        boolean status;

        try {
            //delete room
            status = roomService.deleteRoom(roomUuid);
        } catch (Exception e) {
            throw new ServiceException(e);
        }

        return RestResponseFactory.generateResponse(status, HttpStatus.OK, request, response);
    }
}
