package vip.yazilim.p2g.web.service.p2g.impl.relation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.entity.relation.RoomInvite;
import vip.yazilim.p2g.web.exception.InviteException;
import vip.yazilim.p2g.web.repository.relation.IRoomInviteRepo;
import vip.yazilim.p2g.web.service.p2g.IUserService;
import vip.yazilim.p2g.web.service.p2g.relation.IRoomInviteService;
import vip.yazilim.p2g.web.service.p2g.relation.IRoomUserService;
import vip.yazilim.p2g.web.util.DBHelper;
import vip.yazilim.spring.core.exception.general.InvalidArgumentException;
import vip.yazilim.spring.core.exception.general.InvalidUpdateException;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;
import vip.yazilim.spring.core.exception.general.database.DatabaseReadException;
import vip.yazilim.spring.core.service.ACrudServiceImpl;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 26.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Transactional
@Service
public class RoomInviteService extends ACrudServiceImpl<RoomInvite, String> implements IRoomInviteService {

    // static fields
    private Logger LOGGER = LoggerFactory.getLogger(RoomInviteService.class);

    // injected dependencies
    @Autowired
    private IRoomInviteRepo roomInviteRepo;

    @Autowired
    private IUserService userService;

    @Autowired
    private IRoomUserService roomUserService;

    @Override
    protected JpaRepository<RoomInvite, String> getRepository() {
        return roomInviteRepo;
    }

    @Override
    protected String getId(RoomInvite roomInvite) {
        return roomInvite.getUuid();
    }

    @Override
    protected RoomInvite preInsert(RoomInvite entity) {
        entity.setUuid(DBHelper.getRandomUuid());
        return entity;
    }

    @Override
    public List<User> getInvitedUserListByRoomUuid(String roomUuid) throws DatabaseException {

        List<User> inviteList = new ArrayList<>();
        List<RoomInvite> roomInviteList;

        try {
            roomInviteList = roomInviteRepo.findByRoomUuid(roomUuid);
        } catch (Exception exception) {
            String errorMessage = String.format("An error occurred while getting Invites with roomName[%s]", roomUuid);
            throw new DatabaseReadException(errorMessage, exception);
        }

        for (RoomInvite invite : roomInviteList) {
            Optional<User> user = userService.getUserByUuid(invite.getUuid());
            user.ifPresent(inviteList::add);
        }

        return inviteList;
    }

    @Override
    public RoomInvite accept(RoomInvite roomInvite) throws DatabaseException, InvalidUpdateException, InvalidArgumentException, InviteException {
        roomInvite.setAcceptedFlag(true);
        roomUserService.acceptRoomInvite(roomInvite);
        return update(roomInvite);
    }

    @Override
    public boolean reject(String roomInviteUuid) throws DatabaseException {
        return deleteById(roomInviteUuid);
    }

    @Override
    public boolean deleteRoomInvites(String roomUuid) throws DatabaseException {
        List<RoomInvite> roomInviteList = roomInviteRepo.findByRoomUuid(roomUuid);

        for (RoomInvite roomInvite : roomInviteList) {
            delete(roomInvite);
        }

        return true;
    }

    @Override
    public boolean existsById(String roomInviteUuid) throws DatabaseReadException {
        try {
            return roomInviteRepo.existsById(roomInviteUuid);
        } catch (Exception e) {
            throw new DatabaseReadException(e);
        }
    }

}
