package vip.yazilim.p2g.web.service.p2g.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
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
import vip.yazilim.p2g.web.util.SecurityHelper;
import vip.yazilim.p2g.web.util.TimeHelper;
import vip.yazilim.spring.core.exception.GeneralException;
import vip.yazilim.spring.core.exception.InvalidArgumentException;
import vip.yazilim.spring.core.exception.database.DatabaseException;
import vip.yazilim.spring.core.exception.database.DatabaseReadException;
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
                String frUserId = fr.getSenderId();
                String frFriendIdId = fr.getReceiverId();

                String queryId = frUserId.equals(SecurityHelper.getUserId()) ? frFriendIdId : frUserId;
                fm.setUserModel(userService.getUserModelByUserId(queryId));

                Optional<Room> roomOpt = roomService.getRoomByUserId(userId);
                if (roomOpt.isPresent()) {
                    List<Song> songList = songService.getSongListByRoomId(roomOpt.get().getId());
                    if (!songList.isEmpty()) {
                        fm.setSong(songList.get(0));
                    }
                }

                userModels.add(fm);
            }
        }

        return userModels;
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
    public boolean createFriendRequest(String senderId, String receiverId) throws GeneralException {
        Optional<FriendRequest> existingFriendRequest = friendRequestRepo.findBySenderIdAndReceiverId(senderId, receiverId);

        if (!existingFriendRequest.isPresent()) {
            FriendRequest friendRequest = new FriendRequest();

            friendRequest.setSenderId(senderId);
            friendRequest.setReceiverId(receiverId);
            friendRequest.setRequestDate(TimeHelper.getLocalDateTimeNow());

            create(friendRequest);

            return true;
        } else {
            throw new ConstraintViolationException("Friend request already exists");
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
            throw new NotFoundException("Friend can not found");
        } else {
            FriendRequest friendRequest = friendRequestOpt1.orElseGet(friendRequestOpt2::get);
            return delete(friendRequest);
        }
    }

    private boolean replyFriendRequest(Long friendRequestId, FriendRequestStatus status) throws DatabaseException, InvalidArgumentException {
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
