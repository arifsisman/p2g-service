package vip.yazilim.p2g.web.service.impl.relation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.constant.FriendRequestStatus;
import vip.yazilim.p2g.web.entity.relation.UserFriends;
import vip.yazilim.p2g.web.repository.relation.IUserFriendsRepo;
import vip.yazilim.p2g.web.service.IUserFriendsService;
import vip.yazilim.spring.utils.exception.DatabaseException;
import vip.yazilim.spring.utils.service.ACrudServiceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class UserFriendsService extends ACrudServiceImpl<UserFriends, String> implements IUserFriendsService {

    // static fields
    private Logger LOGGER = LoggerFactory.getLogger(UserFriendsService.class);

    // injected dependencies
    @Autowired
    private IUserFriendsRepo userFriendsRepo;

    @Override
    public List<UserFriends> getUserFriendsByUserUuid(String userUuid) throws DatabaseException {
        List<UserFriends> userFriendsList;

        try {
            userFriendsList = userFriendsRepo.findByUserUuid(userUuid);
        } catch (Exception exception) {
            String errorMessage = String.format("An error occurred while getting User Friends with User[%s]", userUuid);
            throw new DatabaseException(errorMessage, exception);
        }

        return userFriendsList;
    }

    @Override
    public List<UserFriends> getUserFriendRequestsByUserUuid(String userUuid) throws DatabaseException {
        List<UserFriends> userFriendRequestsList;
        //TODO: IMPLEMENT USER FRIEND REQUESTS!
        try {
            userFriendRequestsList = userFriendsRepo.findByUserUuid(userUuid);
        } catch (Exception exception) {
            String errorMessage = String.format("An error occurred while getting User[%s]", userUuid);
            throw new DatabaseException(errorMessage, exception);
        }

        return userFriendRequestsList;
    }

    @Override
    public boolean createUserFriendRequest(String user1, String user2) {
        //TODO: IMPLEMENT USER FRIEND REQUESTS!
        return false;
    }

    @Override
    public boolean replyUserFriendRequest(String user1, String user2, Enum<FriendRequestStatus> statusEnum) {
        //TODO: IMPLEMENT USER FRIEND REQUESTS!
        return false;
    }


    @Override
    protected JpaRepository<UserFriends, String> getRepository() {
        return userFriendsRepo;
    }

    @Override
    protected String getId(UserFriends entity) {
        return entity.getUuid();
    }

}
