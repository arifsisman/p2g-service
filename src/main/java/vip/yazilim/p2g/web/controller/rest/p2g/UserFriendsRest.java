package vip.yazilim.p2g.web.controller.rest.p2g;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vip.yazilim.p2g.web.entity.relation.UserFriends;
import vip.yazilim.p2g.web.service.p2g.IUserFriendsService;
import vip.yazilim.spring.core.rest.ARestCrud;
import vip.yazilim.spring.core.service.ICrudService;

import static vip.yazilim.p2g.web.constant.Constants.API_P2G;

/**
 * @author mustafaarifsisman - 07.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@RestController
@RequestMapping(API_P2G + "/friends")
public class UserFriendsRest extends ARestCrud<UserFriends, String> {

    @Autowired
    private IUserFriendsService userFriendsService;

    @Override
    protected ICrudService<UserFriends, String> getService() {
        return userFriendsService;
    }

}
