package vip.yazilim.p2g.web.service.p2g;

import vip.yazilim.p2g.web.entity.FriendRequest;
import vip.yazilim.p2g.web.model.FriendRequestModel;
import vip.yazilim.p2g.web.model.UserModel;
import vip.yazilim.spring.core.exception.general.InvalidArgumentException;
import vip.yazilim.spring.core.exception.general.InvalidUpdateException;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;
import vip.yazilim.spring.core.exception.general.database.DatabaseReadException;
import vip.yazilim.spring.core.service.ICrudService;

import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 2.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IFriendRequestService extends ICrudService<FriendRequest, Long> {

    List<FriendRequest> getFriendRequestsByUserId(String userId) throws DatabaseException;
    List<UserModel> getFriendsByUserId(String userId) throws DatabaseException, InvalidArgumentException;
    List<FriendRequestModel> getFriendRequestModelByUserId(String userId) throws DatabaseException, InvalidArgumentException;

    Optional<FriendRequest> getFriendRequestByUserAndFriendId(String user1, String user2) throws DatabaseReadException;
    boolean createFriendRequest(String user1, String user2) throws DatabaseException, InvalidArgumentException;
    boolean acceptFriendRequest(Long friendRequestId) throws InvalidUpdateException, DatabaseException, InvalidArgumentException;
    boolean ignoreFriendRequest(Long friendRequestId) throws InvalidUpdateException, DatabaseException, InvalidArgumentException;
    boolean rejectFriendRequest(Long friendRequestId) throws InvalidUpdateException, DatabaseException, InvalidArgumentException;
}
