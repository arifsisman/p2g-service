package vip.yazilim.p2g.web.service.p2g.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.libs.springcore.exception.general.BusinessLogicException;
import vip.yazilim.libs.springcore.exception.general.InvalidArgumentException;
import vip.yazilim.libs.springcore.exception.general.database.DatabaseException;
import vip.yazilim.libs.springcore.exception.general.database.DatabaseReadException;
import vip.yazilim.libs.springcore.exception.service.ResourceNotFoundException;
import vip.yazilim.libs.springcore.service.ACrudServiceImpl;
import vip.yazilim.p2g.web.constant.enums.FriendRequestStatus;
import vip.yazilim.p2g.web.entity.FriendRequest;
import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.p2g.web.exception.ConstraintViolationException;
import vip.yazilim.p2g.web.model.FriendModel;
import vip.yazilim.p2g.web.model.FriendRequestModel;
import vip.yazilim.p2g.web.model.UserModel;
import vip.yazilim.p2g.web.repository.IFriendRequestRepo;
import vip.yazilim.p2g.web.service.p2g.IFriendRequestService;
import vip.yazilim.p2g.web.service.p2g.IRoomService;
import vip.yazilim.p2g.web.service.p2g.ISongService;
import vip.yazilim.p2g.web.service.p2g.IUserService;
import vip.yazilim.p2g.web.util.RoomHelper;
import vip.yazilim.p2g.web.util.SecurityHelper;
import vip.yazilim.p2g.web.util.TimeHelper;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class FriendRequestService extends ACrudServiceImpl<FriendRequest, Long> implements IFriendRequestService {

    @Autowired
    private IFriendRequestRepo friendRequestRepo;

    @Autowired
    private IUserService userService;

    @Autowired
    private IRoomService roomService;

    @Autowired
    private ISongService songService;

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
    public List<FriendModel> getFriendsByUserId(String userId) throws DatabaseException, InvalidArgumentException {
        List<FriendRequest> friendRequestList;
        List<FriendModel> userModels = new LinkedList<>();

        try {
            // Does not matter who sent friend request
            friendRequestList = friendRequestRepo.findBySenderIdOrReceiverId(userId, userId);
        } catch (Exception exception) {
            throw new DatabaseReadException(getClassOfEntity(), exception, userId);
        }

        for (FriendRequest fr : friendRequestList) {
            if (fr.getRequestStatus().equals(FriendRequestStatus.ACCEPTED.getFriendRequestStatus())) {
                FriendModel fm = new FriendModel();
                String senderId = fr.getSenderId();
                String receiverId = fr.getReceiverId();

                //To determine who is friend
                String queryId = senderId.equals(SecurityHelper.getUserId()) ? receiverId : senderId;

                fm.setUserModel(userService.getUserModelByUserId(queryId));

                Optional<Room> roomOpt = roomService.getRoomByUserId(queryId);
                if (roomOpt.isPresent()) {
                    List<Song> songList = songService.getSongListByRoomId(roomOpt.get().getId());
                    fm.setSong(RoomHelper.getRoomCurrentSong(songList));
                }

                userModels.add(fm);
            }
        }

        return userModels;
    }

    @Override
    public Integer getFriendsCountByUserId(String userId) throws DatabaseReadException {
        int count = 0;
        try {
            List<FriendRequest> friendRequestList = friendRequestRepo.findBySenderIdOrReceiverId(userId, userId);
            for (FriendRequest fr : friendRequestList) {
                if (fr.getRequestStatus().equals(FriendRequestStatus.ACCEPTED.getFriendRequestStatus())) {
                    count++;
                }
            }
            return count;
        } catch (Exception exception) {
            throw new DatabaseReadException(getClassOfEntity(), exception, userId);
        }
    }


    @Override
    public List<FriendRequest> getFriendRequestsByReceiverId(String userId) throws DatabaseException {
        try {
            return friendRequestRepo.findByReceiverIdAndRequestStatusNot(userId, FriendRequestStatus.ACCEPTED.getFriendRequestStatus());
        } catch (Exception exception) {
            throw new DatabaseReadException(getClassOfEntity(), exception, userId);
        }
    }

    @Override
    public List<FriendRequestModel> getFriendRequestModelByReceiverId(String userId) throws DatabaseException, InvalidArgumentException {
        List<FriendRequestModel> friendRequestModels = new LinkedList<>();
        List<FriendRequest> friendRequests = getFriendRequestsByReceiverId(userId);

        for (FriendRequest fr : friendRequests) {
            FriendRequestModel frm = new FriendRequestModel();
            frm.setFriendRequest(fr);
            UserModel userModel = userService.getUserModelByUserId(fr.getSenderId());
            frm.setFriendRequestUserModel(userModel);

            friendRequestModels.add(frm);
        }

        return friendRequestModels;
    }

    @Override
    public Optional<FriendRequest> getFriendRequestBySenderIdAndReceiverId(String senderId, String receiverId) throws DatabaseReadException {
        try {
            return friendRequestRepo.findBySenderIdAndReceiverId(senderId, receiverId);
        } catch (Exception exception) {
            throw new DatabaseReadException(getClassOfEntity(), exception, senderId, receiverId);
        }
    }

    @Override
    public Optional<FriendRequest> getFriendRequestBySenderIdAndReceiverIdAndRequestStatus(String senderId, String receiverId, FriendRequestStatus requestStatus) throws DatabaseReadException {
        try {
            return friendRequestRepo.findBySenderIdAndReceiverIdAndRequestStatus(senderId, receiverId, requestStatus.getFriendRequestStatus());
        } catch (Exception exception) {
            throw new DatabaseReadException(getClassOfEntity(), exception, senderId, receiverId);
        }
    }

    @Override
    public boolean createFriendRequest(String senderId, String receiverId) throws BusinessLogicException {
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
            friendRequest.setRequestStatus(FriendRequestStatus.WAITING.getFriendRequestStatus());
            friendRequest.setRequestDate(TimeHelper.getLocalDateTimeNow());

            create(friendRequest);

            return true;
        }
    }

    @Override
    public boolean acceptFriendRequest(Long friendRequestId) throws DatabaseException, InvalidArgumentException {
        return replyFriendRequest(friendRequestId, FriendRequestStatus.ACCEPTED);
    }

    @Override
    public boolean ignoreFriendRequest(Long friendRequestId) throws DatabaseException, InvalidArgumentException {
        return replyFriendRequest(friendRequestId, FriendRequestStatus.IGNORED);
    }

    @Override
    public boolean rejectFriendRequest(Long friendRequestId) throws DatabaseException, InvalidArgumentException {
        return replyFriendRequest(friendRequestId, FriendRequestStatus.REJECTED);
    }

    @Override
    public boolean deleteFriend(String friendId) throws DatabaseException {
        String userId = SecurityHelper.getUserId();
        Optional<FriendRequest> friendRequestOpt1 = getFriendRequestBySenderIdAndReceiverId(friendId, userId);
        Optional<FriendRequest> friendRequestOpt2 = getFriendRequestBySenderIdAndReceiverId(userId, friendId);

        if (!friendRequestOpt1.isPresent() && !friendRequestOpt2.isPresent()) {
            throw new ResourceNotFoundException("Friend can not found");
        } else {
            FriendRequest friendRequest = friendRequestOpt1.orElseGet(friendRequestOpt2::get);
            return delete(friendRequest);
        }
    }

    private boolean replyFriendRequest(Long friendRequestId, FriendRequestStatus status) throws DatabaseException, InvalidArgumentException {
        FriendRequest friendRequest = getById(friendRequestId).orElseThrow(() -> new ResourceNotFoundException("Friend request can not found"));

        if (status == FriendRequestStatus.ACCEPTED) {
            friendRequest.setRequestStatus(status.getFriendRequestStatus());
            update(friendRequest);
        } else {
            delete(friendRequest);
        }

        // if request replied, search for counter friend request then delete matching request
        deleteCounterFriendRequest(friendRequest);

        return true;
    }

    // Search for counter friend request and delete if it exists
    private void deleteCounterFriendRequest(FriendRequest friendRequest) throws DatabaseException {
        String senderId = friendRequest.getSenderId();
        String receiverId = friendRequest.getReceiverId();

        Optional<FriendRequest> counterFriendRequest;

        if (senderId.equals(SecurityHelper.getUserId())) {
            counterFriendRequest = getFriendRequestBySenderIdAndReceiverId(senderId, receiverId);
        } else {
            counterFriendRequest = getFriendRequestBySenderIdAndReceiverId(receiverId, senderId);
        }

        if (counterFriendRequest.isPresent()) {
            delete(counterFriendRequest.get());
        }

    }

}
