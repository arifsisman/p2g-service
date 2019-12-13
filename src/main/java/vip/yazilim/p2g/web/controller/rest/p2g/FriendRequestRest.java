package vip.yazilim.p2g.web.controller.rest.p2g;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vip.yazilim.p2g.web.entity.relation.FriendRequest;
import vip.yazilim.p2g.web.service.p2g.relation.IFriendRequestService;
import vip.yazilim.p2g.web.util.SecurityHelper;
import vip.yazilim.spring.core.exception.web.ServiceException;
import vip.yazilim.spring.core.rest.ARestRead;
import vip.yazilim.spring.core.rest.model.RestResponse;
import vip.yazilim.spring.core.service.ICrudService;
import vip.yazilim.spring.core.util.RestResponseFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static vip.yazilim.p2g.web.constant.Constants.API_P2G;

/**
 * @author mustafaarifsisman - 07.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@RestController
@RequestMapping(API_P2G + "/friends")
public class FriendRequestRest extends ARestRead<FriendRequest, String> {

    @Autowired
    private IFriendRequestService friendRequestService;

    @Override
    protected ICrudService<FriendRequest, String> getService() {
        return friendRequestService;
    }

    @PostMapping("/add/{userUuid}")
    public RestResponse<Boolean> addFriend(HttpServletRequest request, HttpServletResponse response, @PathVariable String userUuid) {
        boolean status;

        try {
            status = friendRequestService.createFriendRequest(SecurityHelper.getUserUuid(), userUuid);
        } catch (Exception e) {
            throw new ServiceException(e);
        }

        return RestResponseFactory.generateResponse(status, HttpStatus.OK, request, response);
    }

    @PutMapping("/accept/{friendRequestUuid}")
    public RestResponse<Boolean> accept(HttpServletRequest request, HttpServletResponse response, @PathVariable String friendRequestUuid) {
        boolean status;

        try {
            status = friendRequestService.acceptFriendRequest(friendRequestUuid);
        } catch (Exception e) {
            throw new ServiceException(e);
        }

        return RestResponseFactory.generateResponse(status, HttpStatus.OK, request, response);
    }

    @PutMapping("/reject/{friendRequestUuid}")
    public RestResponse<Boolean> reject(HttpServletRequest request, HttpServletResponse response, @PathVariable String friendRequestUuid) {
        boolean status;

        try {
            status = friendRequestService.rejectFriendRequest(friendRequestUuid);
        } catch (Exception e) {
            throw new ServiceException(e);
        }

        return RestResponseFactory.generateResponse(status, HttpStatus.OK, request, response);
    }

    @PutMapping("/ignore/{friendRequestUuid}")
    public RestResponse<Boolean> ignore(HttpServletRequest request, HttpServletResponse response, @PathVariable String friendRequestUuid) {
        boolean status;

        try {
            status = friendRequestService.ignoreFriendRequest(friendRequestUuid);
        } catch (Exception e) {
            throw new ServiceException(e);
        }

        return RestResponseFactory.generateResponse(status, HttpStatus.OK, request, response);
    }
}
