package vip.yazilim.p2g.web.rest;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.model.UserModel;
import vip.yazilim.p2g.web.service.p2g.IUserService;
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
@RequestMapping(API_P2G + "/user")
public class UserRest extends ARestCrud<User, String> {

    @Autowired
    private IUserService userService;

    @Override
    protected ICrudService<User, String> getService() {
        return userService;
    }

    @GetMapping({"/{userUuid}/model"})
    @CrossOrigin(origins = {"*"})
    @ApiResponses({@ApiResponse(code = 404, message = "Model not found", response = RestErrorResponse.class), @ApiResponse(code = 500, message = "Internal Error", response = RestErrorResponse.class)})
    public RestResponse<UserModel> getUserModel(HttpServletRequest request, HttpServletResponse response, @PathVariable String userUuid) {
        Optional<UserModel> userModel;

        try {
            userModel = userService.getUserModelByUserUuid(userUuid);
        } catch (Exception var7) {
            throw new ServiceException(var7);
        }

        if (!userModel.isPresent()) {
            throw new NotFoundException("Model Not Found");
        } else {
            return RestResponseFactory.generateResponse(userModel.get(), HttpStatus.OK, request, response);
        }
    }

    @Override
    @PostMapping("/")
    @CrossOrigin(origins = {"*"})
    @ApiResponses({@ApiResponse(code = 500, message = "Internal Error", response = RestErrorResponse.class)})
    public RestResponse<User> create(HttpServletRequest request, HttpServletResponse response, @RequestBody User user) {
        User createdUser;

        try {
            String username = user.getDisplayName();
            String email = user.getEmail();
            String password = user.getPassword();

            createdUser = userService.createUser(username, email, password);
        } catch (Exception var7) {
            throw new ServiceException(var7);
        }

        return RestResponseFactory.generateResponse(createdUser, HttpStatus.OK, request, response);
    }
}
