package vip.yazilim.p2g.web.service.impl.relation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.relation.RoomUser;
import vip.yazilim.p2g.web.exception.InviteException;
import vip.yazilim.p2g.web.repository.relation.IRoomUserRepo;
import vip.yazilim.p2g.web.service.relation.IRoomUserService;
import vip.yazilim.spring.utils.exception.DatabaseException;
import vip.yazilim.spring.utils.service.ACrudServiceImpl;

import java.util.Optional;

/**
 * @author mustafaarifsisman - 2.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class RoomUserServiceImpl extends ACrudServiceImpl<RoomUser, String> implements IRoomUserService {

    // static fields
    private Logger LOGGER = LoggerFactory.getLogger(RoomUserServiceImpl.class);

    // injected dependencies
    @Autowired
    private IRoomUserRepo roomUserRepo;

    @Override
    protected JpaRepository<RoomUser, String> getRepository() {
        return roomUserRepo;
    }

    @Override
    protected String getId(RoomUser entity) {
        return entity.getUuid();
    }

    @Override
    public Optional<RoomUser> getRoomUser(String roomUuid, String userUuid) throws DatabaseException {
        try {
            return roomUserRepo.findByRoomUuidAndUserUuid(roomUuid, userUuid);
        } catch (Exception exception) {
            String errMsg = String.format("An error occurred while getting RoomUser with Room:[%s], User:[%s]", roomUuid,userUuid);
            throw new DatabaseException(errMsg, exception);
        }
    }

    @Override
    public void acceptInviteByUuid(String roomUserUuid) throws DatabaseException, InviteException {
        replyInviteByUuid(roomUserUuid, true);
    }

    @Override
    public void rejectInviteByUuid(String roomUserUuid) throws DatabaseException, InviteException {
        replyInviteByUuid(roomUserUuid, false);
    }

    private void replyInviteByUuid(String roomUserUuid, boolean accepted) throws DatabaseException, InviteException {
        Optional<RoomUser> roomUser = getById(roomUserUuid);

        if (!roomUser.isPresent()) {
            String exceptionMessage = String.format("Invitation cannot send with roomUserUuid[%s]", roomUserUuid);
            throw new InviteException(exceptionMessage);
        }

        roomUser.get().setAcceptedFlag(accepted);

        try {
            roomUserRepo.save(roomUser.get());
        } catch (Exception exception) {
            String errorMessage = String.format("An error occurred while relying invite with roomInviteUuid[%s]", roomUserUuid);
            throw new DatabaseException(errorMessage, exception);
        }
    }
}
