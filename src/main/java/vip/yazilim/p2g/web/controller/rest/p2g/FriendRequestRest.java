package vip.yazilim.p2g.web.controller.rest.p2g;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vip.yazilim.p2g.web.config.annotation.HasSystemRole;
import vip.yazilim.p2g.web.constant.enums.Role;
import vip.yazilim.p2g.web.entity.FriendRequest;
import vip.yazilim.p2g.web.model.FriendRequestModel;
import vip.yazilim.p2g.web.model.UserModel;
import vip.yazilim.p2g.web.service.p2g.IFriendRequestService;
import vip.yazilim.p2g.web.util.SecurityHelper;
import vip.yazilim.spring.core.exception.general.InvalidArgumentException;
import vip.yazilim.spring.core.exception.general.InvalidUpdateException;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;
import vip.yazilim.spring.core.rest.ARestRead;
import vip.yazilim.spring.core.rest.model.RestResponse;
import vip.yazilim.spring.core.service.ICrudService;
import vip.yazilim.spring.core.util.RestResponseFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static vip.yazilim.p2g.web.constant.Constants.API_P2G;

/**
 * @author mustafaarifsisman - 07.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@RestController
@RequestMapping(API_P2G + "/friend/requests")
public class FriendRequestRest extends ARestRead<FriendRequest, Long> {

    @Autowired
    private IFriendRequestService friendRequestService;

    @Override
    protected ICrudService<FriendRequest, Long> getService() {
        return friendRequestService;
    }

    ///////////////////////////////
    // Super class Read controllers
    ///////////////////////////////

    @Override
    @HasSystemRole(role = Role.P2G_USER)
    @GetMapping({"/{id}"})
    public RestResponse<FriendRequest> getById(HttpServletRequest request, HttpServletResponse response, @PathVariable Long id) {
        return super.getById(request, response, id);
    }

    @Override
    @HasSystemRole(role = Role.P2G_USER)
    @GetMapping({"/"})
    public RestResponse<List<FriendRequest>> getAll(HttpServletRequest request, HttpServletResponse response) {
        return super.getAll(request, response);
    }

    ///////////////////////////////
    // Custom controllers
    ///////////////////////////////

    @HasSystemRole(role = Role.P2G_USER)
    @PostMapping("/{userId}/add")
    public RestResponse<Boolean> addFriend(HttpServletRequest request, HttpServletResponse response, @PathVariable String userId) throws DatabaseException, InvalidArgumentException {
        return RestResponseFactory.generateResponse(friendRequestService.createFriendRequest(SecurityHelper.getUserId(), userId), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @DeleteMapping("/{userId}/delete")
    public RestResponse<Boolean> deleteFriend(HttpServletRequest request, HttpServletResponse response, @PathVariable String userId) throws DatabaseException {
        return RestResponseFactory.generateResponse(friendRequestService.deleteFriend(userId), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @PutMapping("/{friendRequestId}/accept")
    public RestResponse<Boolean> accept(HttpServletRequest request, HttpServletResponse response, @PathVariable Long friendRequestId) throws InvalidUpdateException, DatabaseException, InvalidArgumentException {
        return RestResponseFactory.generateResponse(friendRequestService.acceptFriendRequest(friendRequestId), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @PutMapping("/{friendRequestId}/reject")
    public RestResponse<Boolean> reject(HttpServletRequest request, HttpServletResponse response, @PathVariable Long friendRequestId) throws InvalidUpdateException, DatabaseException, InvalidArgumentException {
        return RestResponseFactory.generateResponse(friendRequestService.rejectFriendRequest(friendRequestId), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @PutMapping("/{friendRequestId}/ignore")
    public RestResponse<Boolean> ignore(HttpServletRequest request, HttpServletResponse response, @PathVariable Long friendRequestId) throws InvalidUpdateException, DatabaseException, InvalidArgumentException {
        return RestResponseFactory.generateResponse(friendRequestService.ignoreFriendRequest(friendRequestId), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @GetMapping({"/model"})
    public RestResponse<List<FriendRequestModel>> getFriendRequestModel(HttpServletRequest request, HttpServletResponse response) throws DatabaseException, InvalidArgumentException {
        return RestResponseFactory.generateResponse(friendRequestService.getFriendRequestModelByReceiverId(SecurityHelper.getUserId()), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @GetMapping({"/{userId}/model"})
    public RestResponse<List<FriendRequestModel>> getFriendRequestModel(HttpServletRequest request, HttpServletResponse response, @PathVariable String userId) throws DatabaseException, InvalidArgumentException {
        return RestResponseFactory.generateResponse(friendRequestService.getFriendRequestModelByReceiverId(userId), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @GetMapping({"/friends"})
    public RestResponse<List<UserModel>> getFriends(HttpServletRequest request, HttpServletResponse response) throws DatabaseException, InvalidArgumentException {
        return RestResponseFactory.generateResponse(friendRequestService.getFriendsByUserId(SecurityHelper.getUserId()), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @GetMapping({"/{userId}/friends"})
    public RestResponse<List<UserModel>> getFriends(HttpServletRequest request, HttpServletResponse response, @PathVariable String userId) throws DatabaseException, InvalidArgumentException {
        return RestResponseFactory.generateResponse(friendRequestService.getFriendsByUserId(userId), HttpStatus.OK, request, response);
    }

}
