package vip.yazilim.p2g.web.service.p2g.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.constant.enums.WebSocketDestinations;
import vip.yazilim.p2g.web.controller.websocket.WebSocketController;
import vip.yazilim.p2g.web.entity.RoomInvite;
import vip.yazilim.p2g.web.entity.RoomUser;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.exception.ConstraintViolationException;
import vip.yazilim.p2g.web.model.RoomInviteModel;
import vip.yazilim.p2g.web.model.RoomModelSimplified;
import vip.yazilim.p2g.web.repository.IRoomInviteRepo;
import vip.yazilim.p2g.web.service.p2g.IRoomInviteService;
import vip.yazilim.p2g.web.service.p2g.IRoomService;
import vip.yazilim.p2g.web.service.p2g.IRoomUserService;
import vip.yazilim.p2g.web.service.p2g.IUserService;
import vip.yazilim.p2g.web.util.SecurityHelper;
import vip.yazilim.p2g.web.util.TimeHelper;
import vip.yazilim.spring.core.exception.GeneralException;
import vip.yazilim.spring.core.exception.InvalidArgumentException;
import vip.yazilim.spring.core.exception.database.DatabaseException;
import vip.yazilim.spring.core.exception.database.DatabaseReadException;
import vip.yazilim.spring.core.service.ACrudServiceImpl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 26.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class RoomInviteService extends ACrudServiceImpl<RoomInvite, Long> implements IRoomInviteService {

    @Autowired
    private IRoomInviteRepo roomInviteRepo;

    @Autowired
    private IUserService userService;

    @Autowired
    private IRoomService roomService;

    @Autowired
    private IRoomUserService roomUserService;

    @Autowired
    private WebSocketController webSocketController;

    @Override
    protected JpaRepository<RoomInvite, Long> getRepository() {
        return roomInviteRepo;
    }

    @Override
    protected Long getId(RoomInvite roomInvite) {
        return roomInvite.getId();
    }

    @Override
    protected Class<RoomInvite> getClassOfEntity() {
        return RoomInvite.class;
    }

    @Override
    public List<User> getInvitedUserListByRoomId(Long roomId) throws DatabaseException, InvalidArgumentException {

        List<User> inviteList = new ArrayList<>();
        List<RoomInvite> roomInviteList;

        try {
            roomInviteList = roomInviteRepo.findByRoomId(roomId);
        } catch (Exception exception) {
            throw new DatabaseReadException(getClassOfEntity(), exception, roomId);
        }

        for (RoomInvite invite : roomInviteList) {
            Optional<User> user = userService.getById(invite.getReceiverId());
            user.ifPresent(inviteList::add);
        }

        return inviteList;
    }

    @Override
    public List<RoomInvite> getRoomInvitesByUserId(String userId) throws DatabaseReadException {
        try {
            return roomInviteRepo.findByReceiverId(userId);
        } catch (Exception exception) {
            throw new DatabaseReadException(getClassOfEntity(), exception, userId);
        }
    }

    @Override
    public List<RoomInviteModel> getRoomInviteModelListByUserId(String userId) throws DatabaseException, InvalidArgumentException {
        List<RoomInviteModel> roomInviteModelList = new LinkedList<>();
        List<RoomInvite> roomInvites = getRoomInvitesByUserId(userId);

        for (RoomInvite ri : roomInvites) {
            RoomModelSimplified rm = roomService.getRoomModelSimplifiedByRoomId(ri.getRoomId());
            Optional<User> inviter = userService.getById(ri.getInviterId());

            roomInviteModelList.add(new RoomInviteModel(ri, rm, inviter.orElse(null)));
        }

        return roomInviteModelList;
    }

    @Override
    public RoomInvite invite(Long roomId, String userId) throws GeneralException {
        if (SecurityHelper.getUserId().equals(userId)) {
            throw new ConstraintViolationException("You can not invite yourself.");
        }

        Optional<RoomInvite> existingInvite = roomInviteRepo.findByRoomIdAndReceiverId(roomId, userId);

        if (existingInvite.isPresent()) {
            throw new ConstraintViolationException("Invite already exists");
        } else {
            String inviterId = SecurityHelper.getUserId();

            RoomInvite roomInvite = new RoomInvite();
            roomInvite.setRoomId(roomId);
            roomInvite.setInviterId(inviterId);
            roomInvite.setReceiverId(userId);
            roomInvite.setInvitationDate(TimeHelper.getLocalDateTimeNow());

            RoomInvite createdRoomInvite = create(roomInvite);
            RoomModelSimplified roomModel = roomService.getRoomModelSimplifiedByRoomId(roomId);
            Optional<User> inviter = userService.getById(inviterId);

            RoomInviteModel roomInviteModel = new RoomInviteModel(createdRoomInvite, roomModel, inviter.orElse(null));

            webSocketController.sendToUser(WebSocketDestinations.USER_INVITE.getDestination(), userId, roomInviteModel);
            return createdRoomInvite;
        }
    }

    @Override
    public RoomUser accept(RoomInvite roomInvite) throws GeneralException {
        return roomUserService.acceptRoomInvite(roomInvite);
    }

    @Override
    public boolean reject(Long roomInviteId) throws DatabaseException {
        return deleteById(roomInviteId);
    }

    @Override
    public boolean deleteRoomInvites(Long roomId) throws DatabaseException {
        List<RoomInvite> roomInviteList;

        try {
            roomInviteList = roomInviteRepo.findByRoomId(roomId);
        } catch (Exception exception) {
            throw new DatabaseReadException(getClassOfEntity(), exception, roomId);
        }

        for (RoomInvite roomInvite : roomInviteList) {
            delete(roomInvite);
        }

        return true;
    }

    @Override
    public boolean existsById(Long roomInviteId) throws DatabaseReadException {
        try {
            return roomInviteRepo.existsById(roomInviteId);
        } catch (Exception exception) {
            throw new DatabaseReadException(getClassOfEntity(), exception, roomInviteId);
        }
    }

}
