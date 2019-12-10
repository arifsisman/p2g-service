package vip.yazilim.p2g.web.service.p2g.relation;

import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.entity.relation.FriendRequest;
import vip.yazilim.p2g.web.exception.FriendRequestException;
import vip.yazilim.spring.core.exception.general.InvalidArgumentException;
import vip.yazilim.spring.core.exception.general.InvalidUpdateException;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;
import vip.yazilim.spring.core.service.ICrudService;

import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 2.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IFriendRequestService extends ICrudService<FriendRequest, String> {

    List<User> getFriendRequestByUserUuid(String userUuid) throws DatabaseException, FriendRequestException;
    List<User> getFriendRequestsByUserUuid(String userUuid) throws DatabaseException, FriendRequestException;
    Optional<FriendRequest> getFriendRequestByUserAndFriendUuid(String user1, String user2) throws FriendRequestException;
    boolean createFriendRequest(String user1, String user2) throws FriendRequestException;
    boolean acceptFriendRequest(String friendRequestUuid) throws InvalidUpdateException, DatabaseException, InvalidArgumentException, FriendRequestException;
    boolean ignoreFriendRequest(String friendRequestUuid) throws InvalidUpdateException, DatabaseException, InvalidArgumentException, FriendRequestException;
    boolean rejectFriendRequest(String friendRequestUuid) throws InvalidUpdateException, DatabaseException, InvalidArgumentException, FriendRequestException;
}
