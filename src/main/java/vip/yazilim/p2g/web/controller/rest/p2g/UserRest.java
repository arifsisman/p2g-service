package vip.yazilim.p2g.web.controller.rest.p2g;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vip.yazilim.p2g.web.config.security.annotation.HasSystemRole;
import vip.yazilim.p2g.web.constant.Role;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.model.UserModel;
import vip.yazilim.p2g.web.service.p2g.IUserService;
import vip.yazilim.spring.core.exception.web.NotFoundException;
import vip.yazilim.spring.core.exception.web.ServiceException;
import vip.yazilim.spring.core.rest.ARestCrud;
import vip.yazilim.spring.core.rest.model.RestResponse;
import vip.yazilim.spring.core.service.ICrudService;
import vip.yazilim.spring.core.util.RestResponseFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

import static vip.yazilim.p2g.web.constant.Constants.API_P2G;

/**
 * @author mustafaarifsisman - 5.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@RestController
@RequestMapping(API_P2G + "/user")
public class UserRest extends ARestCrud<User, String> {

    @Autowired
    private IUserService userService;

    @Override
    protected ICrudService<User, String> getService() {
        return userService;
    }

    ///////////////////////////////
    // Super class CRUD controllers
    ///////////////////////////////

    @Override
    @PostMapping({"/"})
    public RestResponse<User> create(HttpServletRequest request, HttpServletResponse response, @RequestBody User entity) {
        return super.create(request, response, entity);
    }

    @Override
    @HasSystemRole(role = Role.P2G_USER)
    @GetMapping({"/{id}"})
    public RestResponse<User> getById(HttpServletRequest request, HttpServletResponse response, @PathVariable String id) {
        return super.getById(request, response, id);
    }

    @Override
    @HasSystemRole(role = Role.P2G_USER)
    @GetMapping({"/"})
    public RestResponse<List<User>> getAll(HttpServletRequest request, HttpServletResponse response) {
        return super.getAll(request, response);
    }

    @Override
    @HasSystemRole(role = Role.P2G_USER)
    @PutMapping({"/"})
    public RestResponse<User> update(HttpServletRequest request, HttpServletResponse response, @RequestBody User entity) {
        return super.update(request, response, entity);
    }

    @Override
    @HasSystemRole(role = Role.P2G_USER)
    @DeleteMapping({"/{id}"})
    public RestResponse<Boolean> delete(HttpServletRequest request, HttpServletResponse response, @PathVariable String id) {
        return super.delete(request, response, id);
    }

    ///////////////////////////////
    // Custom controllers
    ///////////////////////////////

    @HasSystemRole(role = Role.P2G_USER)
    @GetMapping({"/{userUuid}/model"})
    public RestResponse<UserModel> getUserModel(HttpServletRequest request, HttpServletResponse response, @PathVariable String userUuid) {
        Optional<UserModel> userModel;

        try {
            userModel = userService.getUserModelByUserUuid(userUuid);
        } catch (Exception e) {
            throw new ServiceException(e);
        }

        if (!userModel.isPresent()) {
            throw new NotFoundException("Model Not Found");
        } else {
            return RestResponseFactory.generateResponse(userModel.get(), HttpStatus.OK, request, response);
        }
    }
}