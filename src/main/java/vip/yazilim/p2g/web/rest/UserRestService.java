package vip.yazilim.p2g.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.service.p2g.IUserService;
import vip.yazilim.spring.core.rest.ARestCrud;
import vip.yazilim.spring.core.service.ICrudService;

/**
 * @author mustafaarifsisman - 5.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@RestController
@RequestMapping("/p2g/user")
public class UserRestService extends ARestCrud<User, String> {

    @Autowired
    private IUserService userService;

    @Override
    protected ICrudService<User, String> getService() {
        return userService;
    }
}
