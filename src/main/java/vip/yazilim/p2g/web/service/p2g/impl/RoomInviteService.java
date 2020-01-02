package vip.yazilim.p2g.web.service.p2g.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.RoomInvite;
import vip.yazilim.p2g.web.entity.RoomUser;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.repository.IRoomInviteRepo;
import vip.yazilim.p2g.web.service.p2g.IRoomInviteService;
import vip.yazilim.p2g.web.service.p2g.IRoomUserService;
import vip.yazilim.p2g.web.service.p2g.IUserService;
import vip.yazilim.p2g.web.util.TimeHelper;
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
public class RoomInviteService extends ACrudServiceImpl<RoomInvite, Long> implements IRoomInviteService {

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
    protected JpaRepository<RoomInvite, Long> getRepository() {
        return roomInviteRepo;
    }

    @Override
    protected Long getId(RoomInvite roomInvite) {
        return roomInvite.getId();
    }

    @Override
    public List<User> getInvitedUserListByRoomUuid(Long roomUuid) throws DatabaseException, InvalidArgumentException {

        List<User> inviteList = new ArrayList<>();
        List<RoomInvite> roomInviteList;

        try {
            roomInviteList = roomInviteRepo.findByRoomId(roomUuid);
        } catch (Exception exception) {
            String errorMessage = String.format("An error occurred while getting Invites with roomName[%s]", roomUuid);
            throw new DatabaseReadException(errorMessage, exception);
        }

        for (RoomInvite invite : roomInviteList) {
            Optional<User> user = userService.getById(invite.getUserUuid());
            user.ifPresent(inviteList::add);
        }

        return inviteList;
    }

    @Override
    public RoomInvite invite(Long roomUuid, String userUuid) throws DatabaseException, InvalidArgumentException {
        RoomInvite roomInvite = new RoomInvite();
        roomInvite.setRoomId(roomUuid);
        roomInvite.setUserUuid(userUuid);
        roomInvite.setInvitationDate(TimeHelper.getLocalDateTimeNow());
        roomInvite.setAcceptedFlag(false);

        return create(roomInvite);
    }

    @Override
    public RoomUser accept(RoomInvite roomInvite) throws DatabaseException, InvalidUpdateException, InvalidArgumentException {
        roomInvite.setAcceptedFlag(true);
        update(roomInvite);

        return roomUserService.acceptRoomInvite(roomInvite);
    }

    @Override
    public boolean reject(Long roomInviteUuid) throws DatabaseException, InvalidArgumentException {
        return deleteById(roomInviteUuid);
    }

    @Override
    public boolean deleteRoomInvites(Long roomUuid) throws DatabaseException {
        List<RoomInvite> roomInviteList = roomInviteRepo.findByRoomId(roomUuid);

        for (RoomInvite roomInvite : roomInviteList) {
            delete(roomInvite);
        }

        return true;
    }

    @Override
    public boolean existsById(Long roomInviteUuid) throws DatabaseReadException {
        try {
            return roomInviteRepo.existsById(roomInviteUuid);
        } catch (Exception e) {
            throw new DatabaseReadException(e);
        }
    }

}
