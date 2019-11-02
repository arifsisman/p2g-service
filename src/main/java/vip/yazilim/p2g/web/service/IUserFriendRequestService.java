package vip.yazilim.p2g.web.service;

import vip.yazilim.p2g.web.entity.relation.UserFriendRequests;

import java.util.List;

/**
 * @author mustafaarifsisman - 2.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IUserFriendRequestService extends ICrudService<UserFriendRequests, String> {

    List<UserFriendRequests> getUserFriendRequestsByUserUuid(String userUuid);

}
