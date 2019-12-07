package vip.yazilim.p2g.web.controller.rest.p2g;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.model.UserModel;
import vip.yazilim.spring.core.rest.model.RestResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author mustafaarifsisman - 07.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IUserRest {
    RestResponse<UserModel> getUserModel(HttpServletRequest request, HttpServletResponse response, @PathVariable String userUuid);
    RestResponse<User> create(HttpServletRequest request, HttpServletResponse response, @RequestBody User user);

}
