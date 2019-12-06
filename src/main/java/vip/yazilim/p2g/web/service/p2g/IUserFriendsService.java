package vip.yazilim.p2g.web.service.p2g;

import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.entity.relation.UserFriends;
import vip.yazilim.p2g.web.exception.UserFriendsException;
import vip.yazilim.spring.core.exception.InvalidArgumentException;
import vip.yazilim.spring.core.exception.InvalidUpdateException;
import vip.yazilim.spring.core.exception.database.DatabaseException;
import vip.yazilim.spring.core.service.ICrudService;

import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 2.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IUserFriendsService extends ICrudService<UserFriends, String> {

    List<User> getUserFriendsByUserUuid(String userUuid) throws DatabaseException, UserFriendsException;
    List<User> getUserFriendRequestsByUserUuid(String userUuid) throws DatabaseException, UserFriendsException;
    Optional<UserFriends> getUserFriendRequestByUserAndFriendUuid(String user1, String user2) throws UserFriendsException;
    boolean createUserFriendRequest(String user1, String user2) throws UserFriendsException;
    boolean replyUserFriendRequest(String user1, String user2, String status) throws UserFriendsException, DatabaseException, InvalidUpdateException, InvalidArgumentException;

}
