package vip.yazilim.p2g.web.service.p2g.impl.relation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.constant.FriendRequestStatus;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.entity.relation.UserFriends;
import vip.yazilim.p2g.web.exception.UserFriendsException;
import vip.yazilim.p2g.web.repository.relation.IUserFriendsRepo;
import vip.yazilim.p2g.web.service.p2g.IUserFriendsService;
import vip.yazilim.p2g.web.service.p2g.IUserService;
import vip.yazilim.p2g.web.util.DBHelper;
import vip.yazilim.p2g.web.util.TimeHelper;
import vip.yazilim.spring.core.exception.general.InvalidArgumentException;
import vip.yazilim.spring.core.exception.general.InvalidUpdateException;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;
import vip.yazilim.spring.core.service.ACrudServiceImpl;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Transactional
@Service
public class UserFriendsService extends ACrudServiceImpl<UserFriends, String> implements IUserFriendsService {

    // static fields
    private Logger LOGGER = LoggerFactory.getLogger(UserFriendsService.class);

    // injected dependencies
    @Autowired
    private IUserFriendsRepo userFriendsRepo;

    @Autowired
    private IUserService userService;

    @Override
    protected JpaRepository<UserFriends, String> getRepository() {
        return userFriendsRepo;
    }

    @Override
    protected String getId(UserFriends entity) {
        return entity.getUuid();
    }

    @Override
    protected UserFriends preInsert(UserFriends entity) {
        entity.setUuid(DBHelper.getRandomUuid());
        return entity;
    }

    @Override
    public List<User> getUserFriendsByUserUuid(String userUuid) throws UserFriendsException {
        List<UserFriends> userFriendsList;
        List<User> users = new ArrayList<>();

        try {
            userFriendsList = userFriendsRepo.findByUserUuid(userUuid);
        } catch (Exception exception) {
            String errorMessage = String.format("An error occurred while getting Friends for User[%s]", userUuid);
            throw new UserFriendsException(errorMessage, exception);
        }

        for (UserFriends uf : userFriendsList) {
            Optional<User> user = userService.getUserByUuid(uf.getUserUuid());
            user.ifPresent(users::add);
        }

        return users;
    }

    @Override
    public List<User> getUserFriendRequestsByUserUuid(String userUuid) throws UserFriendsException {
        List<UserFriends> userFriendRequestsList;
        List<User> users = new ArrayList<>();

        try {
            userFriendRequestsList = userFriendsRepo.findByUserUuid(userUuid);
        } catch (Exception exception) {
            String errorMessage = String.format("An error occurred while getting Friend Requests for User[%s]", userUuid);
            throw new UserFriendsException(errorMessage, exception);
        }

        for (UserFriends uf : userFriendRequestsList) {
            if (uf.getRequestStatus().equals(FriendRequestStatus.WAITING.toString())) {
                Optional<User> user = userService.getUserByUuid(uf.getUserUuid());
                user.ifPresent(users::add);
            }
        }

        return users;
    }

    @Override
    public Optional<UserFriends> getUserFriendRequestByUserAndFriendUuid(String user1, String user2) throws UserFriendsException {
        UserFriends userFriend;

        try {
            userFriend = userFriendsRepo.findByUserUuidAndFriendUuid(user1, user2);
        } catch (Exception exception) {
            String errorMessage = String.format("An error occurred while getting Friend Requests for User1[%s] and User2[%s]", user1, user2);
            throw new UserFriendsException(errorMessage, exception);
        }

        return Optional.of(userFriend);
    }

    @Override
    public boolean createUserFriendRequest(String user1, String user2) throws UserFriendsException {
        UserFriends friendRequest = new UserFriends();

        friendRequest.setUserUuid(user1);
        friendRequest.setFriendUuid(user2);
        friendRequest.setRequestDate(TimeHelper.getCurrentDate());

        try {
            create(friendRequest);
        } catch (Exception exception) {
            throw new UserFriendsException("Friend request cannot created!", exception);
        }

        return true;
    }

    @Override
    public boolean acceptFriendRequest(String friendRequestUuid) throws InvalidUpdateException, DatabaseException, InvalidArgumentException, UserFriendsException {
        return replyUserFriendRequest(friendRequestUuid, FriendRequestStatus.ACCEPTED);
    }

    @Override
    public boolean ignoreFriendRequest(String friendRequestUuid) throws InvalidUpdateException, DatabaseException, InvalidArgumentException, UserFriendsException {
        return replyUserFriendRequest(friendRequestUuid, FriendRequestStatus.IGNORED);
    }

    @Override
    public boolean rejectFriendRequest(String friendRequestUuid) throws InvalidUpdateException, DatabaseException, InvalidArgumentException, UserFriendsException {
        return replyUserFriendRequest(friendRequestUuid, FriendRequestStatus.REJECTED);
    }

    private boolean replyUserFriendRequest(String friendRequestUuid, FriendRequestStatus status) throws DatabaseException, InvalidUpdateException, InvalidArgumentException, UserFriendsException {

        UserFriends friendRequest = getById(friendRequestUuid).orElseThrow(() -> new UserFriendsException("Friend request can not found"));


        if (status == FriendRequestStatus.ACCEPTED || status == FriendRequestStatus.IGNORED) {
            friendRequest.setRequestStatus(status.getFriendRequestStatus());
            update(friendRequest);
        } else {
            delete(friendRequest);
        }

        return true;
    }

}
