package vip.yazilim.p2g.web.service.p2g;

import vip.yazilim.p2g.web.entity.FriendRequest;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.spring.core.exception.general.InvalidArgumentException;
import vip.yazilim.spring.core.exception.general.InvalidUpdateException;
import vip.yazilim.spring.core.exception.general.database.DatabaseCreateException;
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

    List<User> getFriendsByUserUuid(String userUuid) throws DatabaseException, InvalidArgumentException;
    List<User> getFriendRequestsByUserUuid(String userUuid) throws DatabaseException, InvalidArgumentException;
    Optional<FriendRequest> getFriendRequestByUserAndFriendUuid(String user1, String user2) throws DatabaseReadException;
    boolean createFriendRequest(String user1, String user2) throws DatabaseCreateException;
    boolean acceptFriendRequest(Long friendRequestUuid) throws InvalidUpdateException, DatabaseException, InvalidArgumentException;
    boolean ignoreFriendRequest(Long friendRequestUuid) throws InvalidUpdateException, DatabaseException, InvalidArgumentException;
    boolean rejectFriendRequest(Long friendRequestUuid) throws InvalidUpdateException, DatabaseException, InvalidArgumentException;
}
