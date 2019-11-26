package vip.yazilim.p2g.web.service;

import vip.yazilim.p2g.web.constant.FriendRequestStatus;
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
    List<UserFriends> getUserFriendRequestsByUserUuid(String userUuid) throws DatabaseException;

    boolean createUserFriendRequest(String user1, String user2);
    boolean replyUserFriendRequest(String user1, String user2, Enum<FriendRequestStatus> statusEnum);

}
