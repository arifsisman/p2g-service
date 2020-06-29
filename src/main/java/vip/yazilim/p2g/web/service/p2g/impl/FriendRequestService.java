package vip.yazilim.p2g.web.service.p2g.impl;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.libs.springcore.exception.DatabaseReadException;
import vip.yazilim.libs.springcore.service.ACrudServiceImpl;
import vip.yazilim.p2g.web.entity.FriendRequest;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.enums.FriendRequestStatus;
import vip.yazilim.p2g.web.exception.ConstraintViolationException;
import vip.yazilim.p2g.web.model.FriendRequestModel;
import vip.yazilim.p2g.web.model.UserFriendModel;
import vip.yazilim.p2g.web.model.UserModel;
import vip.yazilim.p2g.web.repository.IFriendRequestRepo;
import vip.yazilim.p2g.web.service.p2g.IFriendRequestService;
import vip.yazilim.p2g.web.service.p2g.IUserService;
import vip.yazilim.p2g.web.util.SecurityHelper;
import vip.yazilim.p2g.web.util.TimeHelper;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class FriendRequestService extends ACrudServiceImpl<FriendRequest, Long> implements IFriendRequestService {

    private final IFriendRequestRepo friendRequestRepo;
    private final IUserService userService;

    public FriendRequestService(IFriendRequestRepo friendRequestRepo, IUserService userService) {
        this.friendRequestRepo = friendRequestRepo;
        this.userService = userService;
    }

    @Override
    protected JpaRepository<FriendRequest, Long> getRepository() {
        return friendRequestRepo;
    }

    @Override
    protected Long getId(FriendRequest entity) {
        return entity.getId();
    }

    @Override
    protected Class<FriendRequest> getClassOfEntity() {
        return FriendRequest.class;
    }

    @Override
    public List<UserModel> getFriendsModelByUserId(String userId) {
        List<FriendRequest> friendRequestList;
        List<UserModel> userModels = new LinkedList<>();

        try {
            // Does not matter who sent friend request
            friendRequestList = friendRequestRepo.findBySenderIdOrReceiverId(userId, userId);
        } catch (Exception exception) {
            throw new DatabaseReadException(getClassOfEntity(), exception, userId);
        }

        for (FriendRequest fr : friendRequestList) {
            if (fr.getRequestStatus().equals(FriendRequestStatus.ACCEPTED.name())) {
                String senderId = fr.getSenderId();
                String receiverId = fr.getReceiverId();

                //To determine who is friend
                String queryId = senderId.equals(SecurityHelper.getUserId()) ? receiverId : senderId;
                userModels.add(userService.getUserModelByUserId(queryId));
            }
        }

        return userModels;
    }

    @Override
    public List<User> getFriendsByUserId(String userId) {
        List<FriendRequest> friendRequestList;
        List<User> friends = new LinkedList<>();

        try {
            // Does not matter who sent friend request
            friendRequestList = friendRequestRepo.findBySenderIdOrReceiverId(userId, userId);
        } catch (Exception exception) {
            throw new DatabaseReadException(getClassOfEntity(), exception, userId);
        }

        for (FriendRequest fr : friendRequestList) {
            if (FriendRequestStatus.valueOf(fr.getRequestStatus()).equals(FriendRequestStatus.ACCEPTED)) {
                Optional<User> userOpt;
                String senderId = fr.getSenderId();
                String receiverId = fr.getReceiverId();

                //To determine who is friend
                String queryId = senderId.equals(SecurityHelper.getUserId()) ? receiverId : senderId;
                userOpt = userService.getById(queryId);
                userOpt.ifPresent(friends::add);
            }
        }

        return friends;
    }

    @Override
    public int getFriendsCountByUserId(String userId) {
        int count = 0;
        try {
            List<FriendRequest> friendRequestList = friendRequestRepo.findBySenderIdOrReceiverId(userId, userId);
            for (FriendRequest fr : friendRequestList) {
                if (FriendRequestStatus.valueOf(fr.getRequestStatus()).equals(FriendRequestStatus.ACCEPTED)) {
                    count++;
                }
            }
            return count;
        } catch (Exception exception) {
            throw new DatabaseReadException(getClassOfEntity(), exception, userId);
        }
    }


    @Override
    public List<FriendRequest> getFriendRequestsByReceiverId(String userId) {
        try {
            return friendRequestRepo.findByReceiverIdAndRequestStatusNot(userId, FriendRequestStatus.ACCEPTED.name());
        } catch (Exception exception) {
            throw new DatabaseReadException(getClassOfEntity(), exception, userId);
        }
    }

    @Override
    public List<FriendRequestModel> getFriendRequestModelByReceiverId(String userId) {
        List<FriendRequestModel> friendRequestModels = new LinkedList<>();
        List<FriendRequest> friendRequests = getFriendRequestsByReceiverId(userId);

        for (FriendRequest fr : friendRequests) {
            FriendRequestModel frm = new FriendRequestModel();
            frm.setFriendRequest(fr);

            String senderId = fr.getSenderId();
            String receiverId = fr.getReceiverId();

            //To determine who is friend
            String queryId = senderId.equals(SecurityHelper.getUserId()) ? receiverId : senderId;
            frm.setUserModel(userService.getUserModelByUserId(queryId));

            friendRequestModels.add(frm);
        }

        return friendRequestModels;
    }

    @Override
    public Optional<FriendRequest> getFriendRequestBySenderIdAndReceiverId(String senderId, String receiverId) {
        try {
            return friendRequestRepo.findBySenderIdAndReceiverId(senderId, receiverId);
        } catch (Exception exception) {
            throw new DatabaseReadException(getClassOfEntity(), exception, senderId, receiverId);
        }
    }

    @Override
    public Optional<FriendRequest> getFriendRequestBySenderIdAndReceiverIdAndRequestStatus(String senderId, String receiverId, FriendRequestStatus requestStatus) {
        try {
            return friendRequestRepo.findBySenderIdAndReceiverIdAndRequestStatus(senderId, receiverId, requestStatus.name());
        } catch (Exception exception) {
            throw new DatabaseReadException(getClassOfEntity(), exception, senderId, receiverId);
        }
    }

    @Override
    public boolean createFriendRequest(String senderId, String receiverId) {
        if (getFriendRequestBySenderIdAndReceiverIdAndRequestStatus(senderId, receiverId, FriendRequestStatus.ACCEPTED).isPresent()) {
            throw new ConstraintViolationException("You are already friends.");
        } else if (getFriendRequestBySenderIdAndReceiverIdAndRequestStatus(receiverId, senderId, FriendRequestStatus.ACCEPTED).isPresent()) {
            throw new ConstraintViolationException("You are already friends.");
        } else if (senderId.equals(receiverId)) {
            throw new ConstraintViolationException("You can not add yourself as friend.");
        } else if (getFriendRequestBySenderIdAndReceiverId(senderId, receiverId).isPresent()) {
            throw new ConstraintViolationException("Friend request already sent.");
        } else {
            FriendRequest friendRequest = new FriendRequest();

            friendRequest.setSenderId(senderId);
            friendRequest.setReceiverId(receiverId);
            friendRequest.setRequestStatus(FriendRequestStatus.WAITING.name());
            friendRequest.setRequestDate(TimeHelper.getLocalDateTimeNow());

            create(friendRequest);

            return true;
        }
    }

    @Override
    public boolean acceptFriendRequest(Long friendRequestId) {
        return replyFriendRequest(friendRequestId, FriendRequestStatus.ACCEPTED);
    }

    @Override
    public boolean ignoreFriendRequest(Long friendRequestId) {
        return replyFriendRequest(friendRequestId, FriendRequestStatus.IGNORED);
    }

    @Override
    public boolean rejectFriendRequest(Long friendRequestId) {
        return replyFriendRequest(friendRequestId, FriendRequestStatus.REJECTED);
    }

    @Override
    public boolean deleteFriend(String friendId) {
        String userId = SecurityHelper.getUserId();
        Optional<FriendRequest> friendRequestOpt1 = getFriendRequestBySenderIdAndReceiverId(friendId, userId);
        Optional<FriendRequest> friendRequestOpt2 = getFriendRequestBySenderIdAndReceiverId(userId, friendId);

        if (!friendRequestOpt1.isPresent() && !friendRequestOpt2.isPresent()) {
            throw new NoSuchElementException("Friend can not found");
        } else {
            FriendRequest friendRequest = friendRequestOpt1.orElseGet(friendRequestOpt2::get);
            return delete(friendRequest);
        }
    }

    @Override
    public UserFriendModel getUserFriendModel(String userId) {
        UserFriendModel userFriendModel = new UserFriendModel();
        userFriendModel.setRequestModels(getFriendRequestModelByReceiverId(userId));
        userFriendModel.setFriendModels(getFriendsModelByUserId(userId));

        return userFriendModel;
    }

    private boolean replyFriendRequest(Long friendRequestId, FriendRequestStatus status) {
        FriendRequest friendRequest = getById(friendRequestId).orElseThrow(() -> new NoSuchElementException("Friend request can not found"));

        if (status == FriendRequestStatus.ACCEPTED) {
            friendRequest.setRequestStatus(status.name());
            update(friendRequest);
        } else {
            delete(friendRequest);
        }

        // if request replied, search for counter friend request then delete matching request
        deleteCounterFriendRequest(friendRequest);

        return true;
    }

    // Search for counter friend request and delete if it exists
    private void deleteCounterFriendRequest(FriendRequest friendRequest) {
        String senderId = friendRequest.getSenderId();
        String receiverId = friendRequest.getReceiverId();

        Optional<FriendRequest> counterFriendRequest;

        if (senderId.equals(SecurityHelper.getUserId())) {
            counterFriendRequest = getFriendRequestBySenderIdAndReceiverId(senderId, receiverId);
        } else {
            counterFriendRequest = getFriendRequestBySenderIdAndReceiverId(receiverId, senderId);
        }

        counterFriendRequest.ifPresent(this::delete);
    }

}
