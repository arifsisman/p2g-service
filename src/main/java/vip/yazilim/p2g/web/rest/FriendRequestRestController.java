package vip.yazilim.p2g.web.rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vip.yazilim.libs.springcore.rest.model.RestResponse;
import vip.yazilim.p2g.web.config.annotation.HasSystemRole;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.enums.Role;
import vip.yazilim.p2g.web.model.UserFriendModel;
import vip.yazilim.p2g.web.service.p2g.IFriendRequestService;
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
@RequestMapping(API_P2G + "/user")
public class FriendRequestRestController {

    private final IFriendRequestService friendRequestService;

    public FriendRequestRestController(IFriendRequestService friendRequestService) {
        this.friendRequestService = friendRequestService;
    }

    @HasSystemRole(role = Role.P2G_USER)
    @PostMapping("/{userId}/add")
    public RestResponse<Boolean> addFriend(HttpServletRequest request, HttpServletResponse response, @PathVariable String userId) {
        return RestResponse.generateResponse(friendRequestService.createFriendRequest(SecurityHelper.getUserId(), userId), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @DeleteMapping("/{userId}/delete")
    public RestResponse<Boolean> deleteFriend(HttpServletRequest request, HttpServletResponse response, @PathVariable String userId) {
        return RestResponse.generateResponse(friendRequestService.deleteFriend(userId), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @GetMapping({"/me/friends/model"})
    public RestResponse<UserFriendModel> getUserFriendModel(HttpServletRequest request, HttpServletResponse response) {
        return RestResponse.generateResponse(friendRequestService.getUserFriendModel(SecurityHelper.getUserId()), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @GetMapping({"/me/friends"})
    public RestResponse<List<User>> getFriends(HttpServletRequest request, HttpServletResponse response) {
        return RestResponse.generateResponse(friendRequestService.getFriendsByUserId(SecurityHelper.getUserId()), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @GetMapping({"/me/friends/counts"})
    public RestResponse<Integer> getFriendsCounts(HttpServletRequest request, HttpServletResponse response) {
        return RestResponse.generateResponse(friendRequestService.getFriendsCountByUserId(SecurityHelper.getUserId()), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @GetMapping({"/{userId}/friends/counts"})
    public RestResponse<Integer> getFriendsCounts(HttpServletRequest request, HttpServletResponse response, @PathVariable String userId) {
        return RestResponse.generateResponse(friendRequestService.getFriendsCountByUserId(userId), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @PutMapping("/friends/{friendRequestId}/accept")
    public RestResponse<Boolean> accept(HttpServletRequest request, HttpServletResponse response, @PathVariable Long friendRequestId) {
        return RestResponse.generateResponse(friendRequestService.acceptFriendRequest(friendRequestId), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @PutMapping("/friends/{friendRequestId}/reject")
    public RestResponse<Boolean> reject(HttpServletRequest request, HttpServletResponse response, @PathVariable Long friendRequestId) {
        return RestResponse.generateResponse(friendRequestService.rejectFriendRequest(friendRequestId), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @PutMapping("/friends/{friendRequestId}/ignore")
    public RestResponse<Boolean> ignore(HttpServletRequest request, HttpServletResponse response, @PathVariable Long friendRequestId) {
        return RestResponse.generateResponse(friendRequestService.ignoreFriendRequest(friendRequestId), HttpStatus.OK, request, response);
    }
}
