package vip.yazilim.p2g.web.service;

import vip.yazilim.p2g.web.entity.relation.UserFriends;
import vip.yazilim.spring.utils.exception.DatabaseException;
import vip.yazilim.spring.utils.service.ICrudService;

import java.util.List;

/**
 * @author mustafaarifsisman - 2.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IUserFriendsService extends ICrudService<UserFriends, String> {

    List<UserFriends> getUserFriendsByUserUuid(String userUuid) throws DatabaseException;

}
