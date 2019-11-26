package vip.yazilim.p2g.web.service.impl.relation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.entity.relation.RoomInvite;
import vip.yazilim.p2g.web.exception.InviteException;
import vip.yazilim.p2g.web.repository.relation.IRoomInviteRepo;
import vip.yazilim.p2g.web.service.IUserService;
import vip.yazilim.p2g.web.service.relation.IRoomInviteService;
import vip.yazilim.spring.utils.exception.DatabaseException;
import vip.yazilim.spring.utils.service.ACrudServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 26.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class RoomInviteServiceImpl extends ACrudServiceImpl<RoomInvite, String> implements IRoomInviteService {

    // static fields
    private Logger LOGGER = LoggerFactory.getLogger(RoomInviteServiceImpl.class);

    // injected dependencies
    @Autowired
    private IRoomInviteRepo roomInviteRepo;

    @Autowired
    private IUserService userService;


    @Override
    public List<User> getInvitedUserListByRoomUuid(String roomUuid) throws DatabaseException {

        List<User> inviteList = new ArrayList<>();
        List<RoomInvite> roomInviteList;

        try {
            roomInviteList = roomInviteRepo.findRoomInvitesByRoomUuid(roomUuid);
        } catch (Exception exception) {
            String errorMessage = String.format("An error occurred while getting Invites with roomName[%s]", roomUuid);
            throw new DatabaseException(errorMessage, exception);
        }

        for (RoomInvite invite:roomInviteList) {
            Optional<User> user = userService.getUserByUuid(invite.getUuid());
            user.ifPresent(inviteList::add);
        }

        return inviteList;
    }

    @Override
    public void acceptInviteByUuid(String roomInviteUuid) throws DatabaseException, InviteException {
        replyInviteByUuid(roomInviteUuid, true);
    }

    @Override
    public void rejectInviteByUuid(String roomInviteUuid) throws DatabaseException, InviteException {
        replyInviteByUuid(roomInviteUuid, false);
    }

    private void replyInviteByUuid(String roomInviteUuid, boolean acceptedFlag) throws DatabaseException, InviteException {
        Optional<RoomInvite> roomInvite = getById(roomInviteUuid);

        if (!roomInvite.isPresent()) {
            String exceptionMessage = String.format("Invitation cannot send with roomInviteUuid[%s]", roomInviteUuid);
            throw new InviteException(exceptionMessage);
        }

        roomInvite.get().setAcceptedFlag(acceptedFlag);

        try {
            roomInviteRepo.save(roomInvite.get());
        } catch (Exception exception) {
            String errorMessage = String.format("An error occurred while relying invite with roomInviteUuid[%s]", roomInviteUuid);
            throw new DatabaseException(errorMessage, exception);
        }
    }

    @Override
    protected JpaRepository<RoomInvite, String> getRepository() {
        return roomInviteRepo;
    }

    @Override
    protected String getId(RoomInvite roomInvite) {
        return roomInvite.getUuid();
    }
}
