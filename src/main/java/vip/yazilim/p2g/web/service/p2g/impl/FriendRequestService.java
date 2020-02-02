package vip.yazilim.p2g.web.service.p2g.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.constant.enums.FriendRequestStatus;
import vip.yazilim.p2g.web.entity.FriendRequest;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.exception.ConstraintViolationException;
import vip.yazilim.p2g.web.model.FriendRequestModel;
import vip.yazilim.p2g.web.model.UserModel;
import vip.yazilim.p2g.web.repository.IFriendRequestRepo;
import vip.yazilim.p2g.web.service.p2g.IFriendRequestService;
import vip.yazilim.p2g.web.service.p2g.IUserService;
import vip.yazilim.p2g.web.util.SecurityHelper;
import vip.yazilim.p2g.web.util.TimeHelper;
import vip.yazilim.spring.core.exception.general.InvalidArgumentException;
import vip.yazilim.spring.core.exception.general.InvalidUpdateException;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;
import vip.yazilim.spring.core.exception.general.database.DatabaseReadException;
import vip.yazilim.spring.core.exception.web.NotFoundException;
import vip.yazilim.spring.core.service.ACrudServiceImpl;

import javax.transaction.Transactional;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Transactional
@Service
public class FriendRequestService extends ACrudServiceImpl<FriendRequest, Long> implements IFriendRequestService {

    @Autowired
    private IFriendRequestRepo friendRequestRepo;

    @Autowired
    private IUserService userService;

    @Override
    protected JpaRepository<FriendRequest, Long> getRepository() {
        return friendRequestRepo;
    }

    @Override
    protected Long getId(FriendRequest entity) {
        return entity.getId();
    }

    @Override
    public List<UserModel> getFriendsByUserId(String userId) throws DatabaseException, InvalidArgumentException {
        List<FriendRequest> friendRequestList;
        List<UserModel> userModels = new LinkedList<>();

        try {
            // Does not matter who sent friend request
            friendRequestList = friendRequestRepo.findByUserIdOrFriendIdAndRequestStatus(userId, userId, FriendRequestStatus.ACCEPTED.getFriendRequestStatus());
        } catch (Exception exception) {
            String errorMessage = String.format("An error occurred while getting Friends for User[%s]", userId);
            throw new DatabaseReadException(errorMessage, exception);
        }

        for (FriendRequest fr : friendRequestList) {
            UserModel um;
            String frUserId = fr.getUserId();
            String frFriendIdId = fr.getFriendId();

            String queryId = frUserId.equals(SecurityHelper.getUserId()) ? frFriendIdId : frUserId;
            um = userService.getUserModelByUserId(queryId);

            userModels.add(um);
        }

        return userModels;
    }

    @Override
    public List<FriendRequest> getFriendRequestsByUserId(String userId) throws DatabaseException {
        try {
            return friendRequestRepo.findByFriendIdAndRequestStatusNot(userId, FriendRequestStatus.ACCEPTED.getFriendRequestStatus());
        } catch (Exception exception) {
            String errorMessage = String.format("An error occurred while getting Friend Requests for User[%s]", userId);
            throw new DatabaseReadException(errorMessage, exception);
        }
    }

    @Override
    public List<User> getFriendRequestsUsersByUserId(String userId) throws DatabaseException, InvalidArgumentException {
        List<FriendRequest> friendRequestList;
        List<User> users = new LinkedList<>();

        friendRequestList = getFriendRequestsByUserId(userId);

        for (FriendRequest uf : friendRequestList) {
            Optional<User> user = userService.getById(uf.getUserId());
            user.ifPresent(users::add);
        }

        return users;
    }

    @Override
    public FriendRequestModel getFriendRequestModelByUserId(String userId) throws DatabaseException, InvalidArgumentException {
        FriendRequestModel friendRequestModel = new FriendRequestModel();

        List<FriendRequest> friendRequests;
        List<User> friendRequestUsers;
        List<UserModel> friends;

        friendRequests = getFriendRequestsByUserId(userId);
        friendRequestModel.setFriendRequests(friendRequests);

        friendRequestUsers = getFriendRequestsUsersByUserId(userId);
        friendRequestModel.setFriendRequestUsers(friendRequestUsers);

        friends = getFriendsByUserId(userId);
        friendRequestModel.setFriends(friends);

        return friendRequestModel;
    }

    @Override
    public Optional<FriendRequest> getFriendRequestByUserAndFriendId(String user1, String user2) throws DatabaseReadException {
        try {
            return friendRequestRepo.findByUserIdAndFriendId(user1, user2);
        } catch (Exception exception) {
            String errorMessage = String.format("An error occurred while getting Friend Requests for User1[%s] and User2[%s]", user1, user2);
            throw new DatabaseReadException(errorMessage, exception);
        }
    }

    @Override
    public boolean createFriendRequest(String user1, String user2) throws DatabaseException, InvalidArgumentException {
        Optional<FriendRequest> existingFriendRequest = friendRequestRepo.findByUserIdAndFriendId(user1, user2);

        if (!existingFriendRequest.isPresent()) {
            FriendRequest friendRequest = new FriendRequest();

            friendRequest.setUserId(user1);
            friendRequest.setFriendId(user2);
            friendRequest.setRequestDate(TimeHelper.getLocalDateTimeNow());

            create(friendRequest);

            return true;
        } else {
            throw new ConstraintViolationException("Friend request already exists");
        }

    }

    @Override
    public boolean acceptFriendRequest(Long friendRequestId) throws InvalidUpdateException, DatabaseException, InvalidArgumentException {
        return replyFriendRequest(friendRequestId, FriendRequestStatus.ACCEPTED);
    }

    @Override
    public boolean ignoreFriendRequest(Long friendRequestId) throws InvalidUpdateException, DatabaseException, InvalidArgumentException {
        return replyFriendRequest(friendRequestId, FriendRequestStatus.IGNORED);
    }

    @Override
    public boolean rejectFriendRequest(Long friendRequestId) throws InvalidUpdateException, DatabaseException, InvalidArgumentException {
        return replyFriendRequest(friendRequestId, FriendRequestStatus.REJECTED);
    }

    private boolean replyFriendRequest(Long friendRequestId, FriendRequestStatus status) throws DatabaseException, InvalidUpdateException, InvalidArgumentException {
        FriendRequest friendRequest = getById(friendRequestId).orElseThrow(() -> new NotFoundException("Friend request can not found"));

        if (status == FriendRequestStatus.ACCEPTED || status == FriendRequestStatus.IGNORED) {
            friendRequest.setRequestStatus(status.getFriendRequestStatus());
            update(friendRequest);
        } else {
            delete(friendRequest);
        }

        //TODO: if request replied, search for user and friend then update or delete existing request

        return true;
    }

}
