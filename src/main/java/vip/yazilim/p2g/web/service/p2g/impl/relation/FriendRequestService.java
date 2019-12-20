package vip.yazilim.p2g.web.service.p2g.impl.relation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.constant.FriendRequestStatus;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.entity.relation.FriendRequest;
import vip.yazilim.p2g.web.repository.relation.IFriendRequestRepo;
import vip.yazilim.p2g.web.service.p2g.IUserService;
import vip.yazilim.p2g.web.service.p2g.relation.IFriendRequestService;
import vip.yazilim.p2g.web.util.DBHelper;
import vip.yazilim.p2g.web.util.TimeHelper;
import vip.yazilim.spring.core.exception.general.InvalidArgumentException;
import vip.yazilim.spring.core.exception.general.InvalidUpdateException;
import vip.yazilim.spring.core.exception.general.database.DatabaseCreateException;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;
import vip.yazilim.spring.core.exception.general.database.DatabaseReadException;
import vip.yazilim.spring.core.exception.web.NotFoundException;
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
public class FriendRequestService extends ACrudServiceImpl<FriendRequest, String> implements IFriendRequestService {

    // static fields
    private Logger LOGGER = LoggerFactory.getLogger(FriendRequestService.class);

    // injected dependencies
    @Autowired
    private IFriendRequestRepo friendRequestRepo;

    @Autowired
    private IUserService userService;

    @Override
    protected JpaRepository<FriendRequest, String> getRepository() {
        return friendRequestRepo;
    }

    @Override
    protected String getId(FriendRequest entity) {
        return entity.getUuid();
    }

    @Override
    protected FriendRequest preInsert(FriendRequest entity) {
        entity.setUuid(DBHelper.getRandomUuid());
        return entity;
    }

    @Override
    public List<User> getFriendsByUserUuid(String userUuid) throws DatabaseReadException {
        List<FriendRequest> friendRequestList;
        List<User> users = new ArrayList<>();

        try {
            friendRequestList = friendRequestRepo.findByUserUuid(userUuid);
        } catch (Exception exception) {
            String errorMessage = String.format("An error occurred while getting Friends for User[%s]", userUuid);
            throw new DatabaseReadException(errorMessage, exception);
        }

        for (FriendRequest uf : friendRequestList) {
            Optional<User> user = userService.getUserByUuid(uf.getUserUuid());
            user.ifPresent(users::add);
        }

        return users;
    }

    @Override
    public List<User> getFriendRequestsByUserUuid(String userUuid) throws DatabaseReadException {
        List<FriendRequest> friendRequestList;
        List<User> users = new ArrayList<>();

        try {
            friendRequestList = friendRequestRepo.findByUserUuid(userUuid);
        } catch (Exception exception) {
            String errorMessage = String.format("An error occurred while getting Friend Requests for User[%s]", userUuid);
            throw new DatabaseReadException(errorMessage, exception);
        }

        for (FriendRequest uf : friendRequestList) {
            if (uf.getRequestStatus().equals(FriendRequestStatus.WAITING.toString())) {
                Optional<User> user = userService.getUserByUuid(uf.getUserUuid());
                user.ifPresent(users::add);
            }
        }

        return users;
    }

    @Override
    public Optional<FriendRequest> getFriendRequestByUserAndFriendUuid(String user1, String user2) throws DatabaseReadException {
        FriendRequest friendRequest;

        try {
            friendRequest = friendRequestRepo.findByUserUuidAndFriendUuid(user1, user2);
        } catch (Exception exception) {
            String errorMessage = String.format("An error occurred while getting Friend Requests for User1[%s] and User2[%s]", user1, user2);
            throw new DatabaseReadException(errorMessage, exception);
        }

        return Optional.of(friendRequest);
    }

    @Override
    public boolean createFriendRequest(String user1, String user2) throws DatabaseCreateException {
        FriendRequest friendRequest = new FriendRequest();

        friendRequest.setUserUuid(user1);
        friendRequest.setFriendUuid(user2);
        friendRequest.setRequestDate(TimeHelper.getLocalDateTimeNow());

        try {
            create(friendRequest);
        } catch (Exception exception) {
            throw new DatabaseCreateException(exception);
        }

        return true;
    }

    @Override
    public boolean acceptFriendRequest(String friendRequestUuid) throws InvalidUpdateException, DatabaseException, InvalidArgumentException {
        return replyFriendRequest(friendRequestUuid, FriendRequestStatus.ACCEPTED);
    }

    @Override
    public boolean ignoreFriendRequest(String friendRequestUuid) throws InvalidUpdateException, DatabaseException, InvalidArgumentException {
        return replyFriendRequest(friendRequestUuid, FriendRequestStatus.IGNORED);
    }

    @Override
    public boolean rejectFriendRequest(String friendRequestUuid) throws InvalidUpdateException, DatabaseException, InvalidArgumentException {
        return replyFriendRequest(friendRequestUuid, FriendRequestStatus.REJECTED);
    }

    private boolean replyFriendRequest(String friendRequestUuid, FriendRequestStatus status) throws DatabaseException, InvalidUpdateException, InvalidArgumentException {

        FriendRequest friendRequest = getById(friendRequestUuid).orElseThrow(() -> new NotFoundException("Friend request can not found"));

        if (status == FriendRequestStatus.ACCEPTED || status == FriendRequestStatus.IGNORED) {
            friendRequest.setRequestStatus(status.getFriendRequestStatus());
            update(friendRequest);
        } else {
            delete(friendRequest);
        }

        return true;
    }

}
