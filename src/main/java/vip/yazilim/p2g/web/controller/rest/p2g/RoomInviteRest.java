//package vip.yazilim.p2g.web.controller.rest.p2g;
//
//import io.swagger.annotations.ApiResponse;
//import io.swagger.annotations.ApiResponses;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.*;
//import vip.yazilim.p2g.web.entity.relation.RoomInvite;
//import vip.yazilim.p2g.web.entity.relation.RoomUser;
//import vip.yazilim.p2g.web.service.p2g.relation.IRoomInviteService;
//import vip.yazilim.p2g.web.service.p2g.relation.IRoomUserService;
//import vip.yazilim.spring.core.exception.web.ServiceException;
//import vip.yazilim.spring.core.rest.ARestRead;
//import vip.yazilim.spring.core.rest.model.RestErrorResponse;
//import vip.yazilim.spring.core.rest.model.RestResponse;
//import vip.yazilim.spring.core.service.ICrudService;
//import vip.yazilim.spring.core.util.RestResponseFactory;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import static vip.yazilim.p2g.web.constant.Constants.API_P2G;
//
///**
// * @author mustafaarifsisman - 07.12.2019
// * @contact mustafaarifsisman@gmail.com
// */
//@RestController
//@RequestMapping(API_P2G + "/invite")
//public class RoomInviteRest extends ARestRead<RoomInvite, String> {
//
//    @Autowired
//    private IRoomInviteService roomInviteService;
//
//    @Autowired
//    private IRoomUserService roomUserService;
//
//    @Override
//    protected ICrudService<RoomInvite, String> getService() {
//        return roomInviteService;
//    }
//
//    @PostMapping("/{roomUuid}/{userUuid}")
//    @CrossOrigin(origins = {"*"})
//    @ApiResponses({@ApiResponse(code = 500, message = "Internal Error", response = RestErrorResponse.class)})
//    public RestResponse<RoomUser> invite(HttpServletRequest request, HttpServletResponse response, @PathVariable String roomUuid, @PathVariable String userUuid) {
//        RoomUser roomUser = new RoomUser();
//
//        roomUser.setRoomUuid(roomUuid);
//        roomUser.setUserUuid(userUuid);
//
//        try {
//            roomUser = roomUserService.create(roomUser);
//        } catch (Exception e) {
//            throw new ServiceException(e);
//        }
//
//        return RestResponseFactory.generateResponse(roomUser, HttpStatus.OK, request, response);
//    }
//
//    @PostMapping("/")
//    @CrossOrigin(origins = {"*"})
//    @ApiResponses({@ApiResponse(code = 500, message = "Internal Error", response = RestErrorResponse.class)})
//    public RestResponse<RoomUser> accept(HttpServletRequest request, HttpServletResponse response, @RequestBody RoomInvite roomInvite) {
//        RoomUser roomUser;
//
//        try {
//            roomUser = roomUserService.acceptRoomInvite(roomInvite);
//        } catch (Exception e) {
//            throw new ServiceException(e);
//        }
//
//        return RestResponseFactory.generateResponse(roomUser, HttpStatus.OK, request, response);
//    }
//
//    @DeleteMapping("/{roomInviteUuid}")
//    @CrossOrigin(origins = {"*"})
//    @ApiResponses({@ApiResponse(code = 404, message = "Entity not found", response = RestErrorResponse.class), @ApiResponse(code = 500, message = "Internal Error", response = RestErrorResponse.class)})
//    public RestResponse<Boolean> reject(HttpServletRequest request, HttpServletResponse response, @PathVariable String roomInviteUuid) {
//        boolean status;
//
//        try {
//            status = roomInviteService.reject(roomInviteUuid);
//        } catch (Exception e) {
//            throw new ServiceException(e);
//        }
//
//        return RestResponseFactory.generateResponse(status, HttpStatus.OK, request, response);
//    }
//}
