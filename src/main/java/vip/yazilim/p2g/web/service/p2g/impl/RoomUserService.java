package vip.yazilim.p2g.web.service.p2g.impl;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.config.security.PasswordEncoderConfig;
import vip.yazilim.p2g.web.config.security.authority.AAuthorityProvider;
import vip.yazilim.p2g.web.constant.enums.Privilege;
import vip.yazilim.p2g.web.constant.enums.Role;
import vip.yazilim.p2g.web.controller.websocket.WebSocketController;
import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.entity.RoomInvite;
import vip.yazilim.p2g.web.entity.RoomUser;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.model.RoomUserModel;
import vip.yazilim.p2g.web.repository.IRoomUserRepo;
import vip.yazilim.p2g.web.service.p2g.IRoomInviteService;
import vip.yazilim.p2g.web.service.p2g.IRoomService;
import vip.yazilim.p2g.web.service.p2g.IRoomUserService;
import vip.yazilim.p2g.web.service.p2g.IUserService;
import vip.yazilim.p2g.web.service.spotify.impl.SpotifyPlayerService;
import vip.yazilim.p2g.web.util.SecurityHelper;
import vip.yazilim.p2g.web.util.TimeHelper;
import vip.yazilim.spring.core.exception.GeneralException;
import vip.yazilim.spring.core.exception.InvalidArgumentException;
import vip.yazilim.spring.core.exception.database.DatabaseException;
import vip.yazilim.spring.core.exception.database.DatabaseReadException;
import vip.yazilim.spring.core.exception.web.NotFoundException;
import vip.yazilim.spring.core.service.ACrudServiceImpl;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 2.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class RoomUserService extends ACrudServiceImpl<RoomUser, Long> implements IRoomUserService {

    private Logger LOGGER = LoggerFactory.getLogger(RoomUserService.class);

    @Autowired
    private IRoomUserRepo roomUserRepo;

    @Autowired
    private IRoomService roomService;

    @Autowired
    private IRoomInviteService roomInviteService;

    @Autowired
    private AAuthorityProvider authorityProvider;

    @Autowired
    private PasswordEncoderConfig passwordEncoderConfig;

    @Autowired
    private SpotifyPlayerService spotifyPlayerService;

    @Autowired
    private IUserService userService;

    @Autowired
    private WebSocketController webSocketController;

    @Override
    protected JpaRepository<RoomUser, Long> getRepository() {
        return roomUserRepo;
    }

    @Override
    protected Long getId(RoomUser entity) {
        return entity.getId();
    }

    @Override
    protected Class<RoomUser> getClassOfEntity() {
        return RoomUser.class;
    }

    @Override
    protected RoomUser preInsert(RoomUser entity) {
        entity.setJoinDate(TimeHelper.getLocalDateTimeNow());
        return entity;
    }

    @Override
    public List<RoomUser> getRoomUsersByRoomId(Long roomId) throws DatabaseException {
        try {
            return roomUserRepo.findRoomUserByRoomIdOrderById(roomId);
        } catch (Exception exception) {
            throw new DatabaseReadException(getClassOfEntity(), exception, roomId);
        }
    }

    @Override
    public Optional<RoomUser> getRoomUser(String userId) throws DatabaseException {
        try {
            return roomUserRepo.findRoomUserByUserId(userId);
        } catch (Exception exception) {
            throw new DatabaseReadException(getClassOfEntity(), exception, userId);
        }
    }

    @Override
    public RoomUserModel getRoomUserModelMe(String userId) throws DatabaseException, InvalidArgumentException {
        Optional<RoomUser> roomUserOpt = getRoomUser(userId);
        if (roomUserOpt.isPresent()) {
            RoomUserModel roomUserModel = new RoomUserModel();
            roomUserModel.setRoomUser(roomUserOpt.get());
            userService.getById(userId).ifPresent(roomUserModel::setUser);
            return roomUserModel;
        } else {
            String msg = String.format("User[%s] not in any room", userId);
            throw new NotFoundException(msg);
        }
    }

    @Override
    public Optional<RoomUser> getRoomUser(Long roomId, String userId) throws DatabaseException {
        try {
            return roomUserRepo.findRoomUserByRoomIdAndUserId(roomId, userId);
        } catch (Exception exception) {
            throw new DatabaseReadException(getClassOfEntity(), exception, roomId, userId);
        }
    }

    @Override
    public Optional<RoomUser> getRoomOwner(Long roomId) throws DatabaseException {
        try {
            return roomUserRepo.findRoomUserByRoomIdAndRole(roomId, Role.ROOM_OWNER.role);
        } catch (Exception exception) {
            throw new DatabaseReadException(getClassOfEntity(), exception, roomId);
        }
    }

    /**
     * If any old room exists, leave room
     * Else if any old room exists and user is owner, delete room
     *
     * @param roomId   roomId
     * @param password password
     * @param role     role
     * @return RoomUser
     * @throws GeneralException       GeneralException
     * @throws IOException            IOException
     * @throws SpotifyWebApiException SpotifyWebApiException
     */
    @Override
    public RoomUser joinRoom(Long roomId, String userId, String password, Role role) throws GeneralException, IOException, SpotifyWebApiException {
        Optional<Room> roomOpt = roomService.getById(roomId);
        if (!roomOpt.isPresent()) {
            String err = String.format("Room[%s] can not found", roomId);
            throw new InvalidArgumentException(err);
        } else {
            // Any room exists check
            Optional<RoomUser> existingUserOpt = getRoomUser(userId);
            if (existingUserOpt.isPresent()) {
                leaveRoom();
            }

            // Normal condition
            Room room = roomOpt.get();
            RoomUser roomUser = new RoomUser();

            if (room.getPassword() == null || room.getPassword().equals("") || passwordEncoderConfig.passwordEncoder().matches(password.replace("\"", ""), room.getPassword())) {
                roomUser.setRoomId(roomId);
                roomUser.setUserId(userId);
                roomUser.setRole(role.getRole());
                roomUser.setActiveFlag(true);
            } else {
                throw new InvalidArgumentException("Wrong password");
            }

            RoomUser joinedUser = create(roomUser);
            spotifyPlayerService.userSyncWithRoom(joinedUser);

            LOGGER.info("User[{}] joined Room[{}]", userId, roomId);
            return joinedUser;
        }
    }

    @Override
    public RoomUser joinRoomOwner(Long roomId, String userId) throws GeneralException {
        RoomUser roomUser = new RoomUser();

        roomUser.setRoomId(roomId);
        roomUser.setUserId(userId);
        roomUser.setRole(Role.ROOM_OWNER.getRole());
        roomUser.setActiveFlag(true);

        return super.create(roomUser);
    }

    @Override
    public boolean leaveRoom() throws DatabaseException {
        String userId = SecurityHelper.getUserId();
        Optional<RoomUser> roomUserOpt = getRoomUser(userId);

        if (roomUserOpt.isPresent()) {
            RoomUser roomUser = roomUserOpt.get();
            if (roomUserOpt.get().getRole().equals(Role.ROOM_OWNER.getRole())) {
                boolean status = roomService.deleteById(roomUser.getRoomId());
                if (status) {
                    LOGGER.info("Room[{}] closed by User[{}]", roomUser.getRoomId(), userId);
                }
                return status;
            } else {
                // Update room users before user leaves
                updateRoomUsers(roomUserOpt.get());

                boolean status = delete(roomUser);
                if (status) {
                    LOGGER.info("User[{}] leaved Room[{}]", userId, roomUser.getRoomId());
                }

                try {
                    spotifyPlayerService.userDeSyncWithRoom(roomUser);
                } catch (IOException | SpotifyWebApiException e) {
                    LOGGER.info("An error occurred when User[{}] desync with Room[{}]", userId, roomUser.getRoomId());
                }

                return status;
            }
        } else {
            throw new NotFoundException("User not in any room");
        }
    }

    @Override
    public List<RoomUserModel> getRoomUserModelsByRoomId(Long roomId) throws DatabaseException {
        List<RoomUserModel> roomUserModels = new LinkedList<>();
        List<RoomUser> roomUsers = getRoomUsersByRoomId(roomId);

        for (RoomUser ru : roomUsers) {
            RoomUserModel roomUserModel = new RoomUserModel();
            roomUserModel.setRoomUser(ru);
            Optional<User> roomUserOpt = userService.getUserById(ru.getUserId());
            roomUserOpt.ifPresent(roomUserModel::setUser);
            roomUserModels.add(roomUserModel);
        }

        return roomUserModels;
    }

    @Override
    public RoomUser acceptRoomInvite(RoomInvite roomInvite) throws GeneralException {
        if (!roomInviteService.existsById(roomInvite.getId())) {
            String err = String.format("Room Invite[%s] not found", roomInvite.getId());
            throw new NotFoundException(err);
        } else {
            // Any room exists check
            Optional<RoomUser> existingUserOpt = getRoomUser(roomInvite.getReceiverId());
            if (existingUserOpt.isPresent()) {
                leaveRoom();
            }

            RoomUser roomUser = new RoomUser();

            roomUser.setRoomId(roomInvite.getRoomId());
            roomUser.setUserId(roomInvite.getReceiverId());
            roomUser.setRole(Role.ROOM_USER.getRole());
            roomUser.setActiveFlag(true);

            RoomUser createdRoomUser = create(roomUser);
            roomInviteService.delete(roomInvite);

            LOGGER.info("User[{}] accepted Room[{}] invite from User[{}]", roomInvite.getReceiverId(), roomInvite.getRoomId(), roomInvite.getInviterId());
            return createdRoomUser;
        }
    }

    @Override
    public Role getRoleByRoomIdAndUserId(Long roomId, String userId) throws DatabaseException {
        Optional<RoomUser> roomUserOpt = getRoomUser(userId);

        if (!roomUserOpt.isPresent()) {
            return null;
        }

        String role = roomUserOpt.get().getRole();
        return Role.getRole(role);
    }

    @Override
    public boolean deleteRoomUsers(Long roomId) throws DatabaseException {
        List<RoomUser> roomUserList;

        try {
            roomUserList = roomUserRepo.findRoomUserByRoomIdOrderById(roomId);
        } catch (Exception exception) {
            throw new DatabaseReadException(getClassOfEntity(), exception, roomId);
        }

        for (RoomUser roomUser : roomUserList) {
            delete(roomUser);
        }

        return true;
    }

    @Override
    public RoomUser promoteUserRole(Long roomUserId) throws DatabaseException, InvalidArgumentException {
        RoomUser roomUser = getSafeRoomUser(roomUserId);

        Role oldRole = Role.getRole(roomUser.getRole());
        Role newRole;

        switch (oldRole) {
            case ROOM_USER:
                newRole = Role.ROOM_MODERATOR;
                break;
            case ROOM_MODERATOR:
                newRole = Role.ROOM_ADMIN;
                break;
            default:
                newRole = oldRole;
                break;
        }

        roomUser.setRole(newRole.getRole());
        return update(roomUser);
    }

    @Override
    public RoomUser demoteUserRole(Long roomUserId) throws DatabaseException, InvalidArgumentException {
        RoomUser roomUser = getSafeRoomUser(roomUserId);

        Role oldRole = Role.getRole(roomUser.getRole());
        Role newRole;

        switch (oldRole) {
            case ROOM_MODERATOR:
                newRole = Role.ROOM_USER;
                break;
            case ROOM_ADMIN:
                newRole = Role.ROOM_MODERATOR;
                break;
            default:
                newRole = oldRole;
                break;
        }

        roomUser.setRole(newRole.getRole());
        return update(roomUser);
    }

    @Override
    public boolean hasRoomPrivilege(String userId, Privilege privilege) throws DatabaseException {
        Optional<RoomUser> roomUserOpt = getRoomUser(userId);
        return roomUserOpt.isPresent() && authorityProvider.hasPrivilege(Role.getRole(roomUserOpt.get().getRole()), privilege);
    }

    @Override
    public boolean hasRoomRole(String userId, Role role) throws DatabaseException {
        Optional<RoomUser> roomUserOpt = getRoomUser(userId);
        return roomUserOpt.isPresent() && role.equals(Role.getRole(roomUserOpt.get().getRole()));
    }

    @Override
    public int getRoomUserCountByRoomId(Long roomId) throws DatabaseReadException {
        try {
            return roomUserRepo.countRoomUsersByRoomId(roomId);
        } catch (Exception exception) {
            throw new DatabaseReadException(getClassOfEntity(), exception, roomId);
        }
    }

    private RoomUser getSafeRoomUser(Long roomUserId) throws DatabaseException, InvalidArgumentException {
        Optional<RoomUser> roomUserOpt = getById(roomUserId);

        if (roomUserOpt.isPresent()) {
            return roomUserOpt.get();
        } else {
            throw new NotFoundException("Room user not found");
        }
    }

    private void updateRoomUsers(RoomUser roomUser) throws DatabaseException {
        Long roomId = roomUser.getRoomId();
        List<RoomUserModel> roomUserModels = getRoomUserModelsByRoomId(roomId);
        roomUserModels.removeIf(roomUserModel -> roomUserModel.getRoomUser() == roomUser);
        webSocketController.sendToRoom("users", roomId, roomUserModels);
    }
}
