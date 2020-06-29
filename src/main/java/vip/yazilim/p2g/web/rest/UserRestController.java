package vip.yazilim.p2g.web.rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vip.yazilim.libs.springcore.rest.model.RestResponse;
import vip.yazilim.p2g.web.config.annotation.HasSystemPrivilege;
import vip.yazilim.p2g.web.config.annotation.HasSystemRole;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.enums.Privilege;
import vip.yazilim.p2g.web.enums.Role;
import vip.yazilim.p2g.web.model.UserModel;
import vip.yazilim.p2g.web.service.p2g.IUserService;
import vip.yazilim.p2g.web.util.SecurityHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static vip.yazilim.p2g.web.constant.Constants.API_P2G;

/**
 * @author mustafaarifsisman - 5.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@RestController
@RequestMapping(API_P2G + "/user")
public class UserRestController {

    private final IUserService userService;

    public UserRestController(IUserService userService) {
        this.userService = userService;
    }

    @HasSystemRole(role = Role.P2G_USER)
    @GetMapping({"/{userId}"})
    public RestResponse<User> getById(HttpServletRequest request, HttpServletResponse response, @PathVariable String userId) {
        Optional<User> user = userService.getById(userId);
        if (user.isPresent()) {
            return RestResponse.generateResponse(user.get(), HttpStatus.OK, request, response);
        } else {
            throw new NoSuchElementException("User :: Not Found :: " + userId);
        }
    }

    @HasSystemRole(role = Role.P2G_USER)
    @GetMapping({"/"})
    public RestResponse<List<User>> getAll(HttpServletRequest request, HttpServletResponse response) {
        return RestResponse.generateResponse(userService.getAll(), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @GetMapping({"/{userId}/model"})
    public RestResponse<UserModel> getUserModel(HttpServletRequest request, HttpServletResponse response, @PathVariable String userId) {
        return RestResponse.generateResponse(userService.getUserModelByUserId(userId), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @GetMapping({"/me/model"})
    public RestResponse<UserModel> getUserModelMe(HttpServletRequest request, HttpServletResponse response) {
        return RestResponse.generateResponse(userService.getUserModelByUserId(SecurityHelper.getUserId()), HttpStatus.OK, request, response);
    }

    @HasSystemPrivilege(privilege = Privilege.ROOM_INVITE_AND_REPLY)
    @GetMapping({"/search/name/{userNameQuery}"})
    public RestResponse<List<User>> getUsersNameLike(HttpServletRequest request, HttpServletResponse response, @PathVariable String userNameQuery) {
        return RestResponse.generateResponse(userService.getUsersNameLike(userNameQuery), HttpStatus.OK, request, response);
    }

}
