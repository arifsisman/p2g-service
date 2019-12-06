package vip.yazilim.p2g.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.exception.RoleException;
import vip.yazilim.p2g.web.exception.RoomException;
import vip.yazilim.p2g.web.model.UserModel;
import vip.yazilim.p2g.web.service.p2g.IUserService;
import vip.yazilim.spring.core.exception.InvalidArgumentException;
import vip.yazilim.spring.core.exception.database.DatabaseException;
import vip.yazilim.spring.core.rest.ARestCrud;
import vip.yazilim.spring.core.service.ICrudService;

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

    @GetMapping("/model/{userUuid}")
    public Optional<UserModel> getRoomModel(@PathVariable String userUuid) throws InvalidArgumentException, RoleException, RoomException, DatabaseException {
        return userService.getUserModelByUserUuid(userUuid);
    }
}
