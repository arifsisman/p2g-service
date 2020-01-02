package vip.yazilim.p2g.web.service.p2g.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.constant.FriendRequestStatus;
import vip.yazilim.p2g.web.entity.FriendRequest;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.repository.IFriendRequestRepo;
import vip.yazilim.p2g.web.service.p2g.IFriendRequestService;
import vip.yazilim.p2g.web.service.p2g.IUserService;
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
import java.util.UUID;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Transactional
@Service
public class FriendRequestService extends ACrudServiceImpl<FriendRequest, Long> implements IFriendRequestService {

    // static fields
    private Logger LOGGER = LoggerFactory.getLogger(FriendRequestService.class);

    // injected dependencies
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
    public List<User> getFriendsByUserUuid(UUID userUuid) throws DatabaseException, InvalidArgumentException {
        List<FriendRequest> friendRequestList;
        List<User> users = new ArrayList<>();

        try {
            friendRequestList = friendRequestRepo.findByUserUuid(userUuid);
        } catch (Exception exception) {
            String errorMessage = String.format("An error occurred while getting Friends for User[%s]", userUuid);
            throw new DatabaseReadException(errorMessage, exception);
        }

        for (FriendRequest uf : friendRequestList) {
            Optional<User> user = userService.getById(uf.getUserUuid());
            user.ifPresent(users::add);
        }

        return users;
    }

    @Override
    public List<User> getFriendRequestsByUserUuid(UUID userUuid) throws DatabaseException, InvalidArgumentException {
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
                Optional<User> user = userService.getById(uf.getUserUuid());
                user.ifPresent(users::add);
            }
        }

        return users;
    }

    @Override
    public Optional<FriendRequest> getFriendRequestByUserAndFriendUuid(UUID user1, UUID user2) throws DatabaseReadException {
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
    public boolean createFriendRequest(UUID user1, UUID user2) throws DatabaseCreateException {
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
    public boolean acceptFriendRequest(Long friendRequestUuid) throws InvalidUpdateException, DatabaseException, InvalidArgumentException {
        return replyFriendRequest(friendRequestUuid, FriendRequestStatus.ACCEPTED);
    }

    @Override
    public boolean ignoreFriendRequest(Long friendRequestUuid) throws InvalidUpdateException, DatabaseException, InvalidArgumentException {
        return replyFriendRequest(friendRequestUuid, FriendRequestStatus.IGNORED);
    }

    @Override
    public boolean rejectFriendRequest(Long friendRequestUuid) throws InvalidUpdateException, DatabaseException, InvalidArgumentException {
        return replyFriendRequest(friendRequestUuid, FriendRequestStatus.REJECTED);
    }

    private boolean replyFriendRequest(Long friendRequestUuid, FriendRequestStatus status) throws DatabaseException, InvalidUpdateException, InvalidArgumentException {

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
