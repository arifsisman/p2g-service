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
            friendRequestList = friendRequestRepo.findBySenderIdOrReceiverId(userId, userId);
        } catch (Exception exception) {
            String errorMessage = String.format("An error occurred while getting Friends for User[%s]", userId);
            throw new DatabaseReadException(errorMessage, exception);
        }

        for (FriendRequest fr : friendRequestList) {
            if (fr.getRequestStatus().equals(FriendRequestStatus.ACCEPTED.getFriendRequestStatus())) {
                UserModel um;
                String frUserId = fr.getSenderId();
                String frFriendIdId = fr.getReceiverId();

                String queryId = frUserId.equals(SecurityHelper.getUserId()) ? frFriendIdId : frUserId;
                um = userService.getUserModelByUserId(queryId);
                userModels.add(um);
            }
        }

        return userModels;
    }

    @Override
    public List<FriendRequest> getFriendRequestsByReceiverId(String userId) throws DatabaseException {
        try {
            return friendRequestRepo.findByReceiverIdAndRequestStatusNot(userId, FriendRequestStatus.ACCEPTED.getFriendRequestStatus());
        } catch (Exception exception) {
            String errorMessage = String.format("An error occurred while getting Friend Requests for User[%s]", userId);
            throw new DatabaseReadException(errorMessage, exception);
        }
    }

    @Override
    public List<FriendRequestModel> getFriendRequestModelByReceiverId(String userId) throws DatabaseException, InvalidArgumentException {
        List<FriendRequestModel> friendRequestModels = new LinkedList<>();
        List<FriendRequest> friendRequests = getFriendRequestsByReceiverId(userId);

        for (FriendRequest fr : friendRequests) {
            FriendRequestModel frm = new FriendRequestModel();
            frm.setFriendRequest(fr);
            Optional<User> userOpt = userService.getById(fr.getSenderId());
            userOpt.ifPresent(frm::setFriendRequestUser);

            friendRequestModels.add(frm);
        }

        return friendRequestModels;
    }

    @Override
    public Optional<FriendRequest> getFriendRequestBySenderIdAndReceiverId(String senderId, String receiverId) throws DatabaseReadException {
        try {
            return friendRequestRepo.findBySenderIdAndReceiverId(senderId, receiverId);
        } catch (Exception exception) {
            String errorMessage = String.format("An error occurred while getting Friend Requests for User1[%s] and User2[%s]", senderId, receiverId);
            throw new DatabaseReadException(errorMessage, exception);
        }
    }

    @Override
    public boolean createFriendRequest(String user1, String user2) throws DatabaseException, InvalidArgumentException {
        Optional<FriendRequest> existingFriendRequest = friendRequestRepo.findBySenderIdAndReceiverId(user1, user2);

        if (!existingFriendRequest.isPresent()) {
            FriendRequest friendRequest = new FriendRequest();

            friendRequest.setSenderId(user1);
            friendRequest.setReceiverId(user2);
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

        if (status == FriendRequestStatus.ACCEPTED) {
            friendRequest.setRequestStatus(status.getFriendRequestStatus());
            update(friendRequest);
        } else {
            delete(friendRequest);
        }

        // if request replied, search for counter friend request then delete matching request
        return deleteCounterFriendRequest(friendRequest);
    }

    private boolean deleteCounterFriendRequest(FriendRequest friendRequest) throws DatabaseException {
        // Search for counter friend request and delete if it exists
        Optional<FriendRequest> counterFriendRequest = getFriendRequestBySenderIdAndReceiverId(friendRequest.getReceiverId(), friendRequest.getSenderId());

        if (counterFriendRequest.isPresent()) {
            return delete(counterFriendRequest.get());
        }

        return true;
    }

}
