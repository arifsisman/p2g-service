package vip.yazilim.p2g.web.rest;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vip.yazilim.p2g.web.entity.relation.RoomQueue;
import vip.yazilim.p2g.web.model.SearchModel;
import vip.yazilim.p2g.web.service.p2g.relation.IRoomQueueService;
import vip.yazilim.spring.core.exception.web.ServiceException;
import vip.yazilim.spring.core.rest.ARestCrud;
import vip.yazilim.spring.core.rest.model.RestErrorResponse;
import vip.yazilim.spring.core.rest.model.RestResponse;
import vip.yazilim.spring.core.rest.model.RestResponseFactory;
import vip.yazilim.spring.core.service.ICrudService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static vip.yazilim.p2g.web.constant.Constants.API_P2G;

/**
 * @author mustafaarifsisman - 06.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@RestController
@RequestMapping(API_P2G + "/queue")
public class RoomQueueRest extends ARestCrud<RoomQueue, String> {

    @Autowired
    private IRoomQueueService roomQueueService;

    @Override
    protected ICrudService<RoomQueue, String> getService() {
        return roomQueueService;
    }

    @GetMapping("/{roomUuid}/model")
    @CrossOrigin(origins = {"*"})
    @ApiResponses({@ApiResponse(code = 404, message = "Entity not found", response = RestErrorResponse.class), @ApiResponse(code = 500, message = "Internal Error", response = RestErrorResponse.class)})
    public RestResponse<List<RoomQueue>> getRoomQueueListByRoomUuid(HttpServletRequest request, HttpServletResponse response, @PathVariable String roomUuid) {
        List<RoomQueue> roomQueueList;

        try {
            roomQueueList = roomQueueService.getRoomQueueListByRoomUuid(roomUuid);
        } catch (Exception var7) {
            throw new ServiceException(var7);
        }

        return RestResponseFactory.generateResponse(roomQueueList, HttpStatus.OK, request, response);
    }

    @PostMapping("/{roomUuid}/{searchModel}")
    @CrossOrigin(origins = {"*"})
    @ApiResponses({@ApiResponse(code = 500, message = "Internal Error", response = RestErrorResponse.class)})
    public RestResponse<RoomQueue> addToRoomQueue(HttpServletRequest request, HttpServletResponse response, @PathVariable String roomUuid, @PathVariable SearchModel searchModel) {
        RoomQueue roomQueue;

        try {
            roomQueue = roomQueueService.addToRoomQueue(roomUuid, searchModel);
        } catch (Exception var7) {
            throw new ServiceException(var7);
        }

        return RestResponseFactory.generateResponse(roomQueue, HttpStatus.OK, request, response);
    }
}
