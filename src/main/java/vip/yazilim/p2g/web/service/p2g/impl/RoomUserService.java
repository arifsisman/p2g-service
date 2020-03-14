package vip.yazilim.p2g.web.service.p2g.impl;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.libs.springcore.exception.general.BusinessLogicException;
import vip.yazilim.libs.springcore.exception.general.InvalidArgumentException;
import vip.yazilim.libs.springcore.exception.general.database.DatabaseException;
import vip.yazilim.libs.springcore.exception.service.ResourceNotFoundException;
import vip.yazilim.libs.springcore.service.ACrudServiceImpl;
import vip.yazilim.p2g.web.config.security.PasswordEncoderConfig;
import vip.yazilim.p2g.web.config.security.authority.AAuthorityProvider;
import vip.yazilim.p2g.web.constant.enums.Privilege;
import vip.yazilim.p2g.web.constant.enums.Role;
import vip.yazilim.p2g.web.controller.websocket.WebSocketController;
import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.entity.RoomInvite;
import vip.yazilim.p2g.web.entity.RoomUser;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.exception.ConstraintViolationException;
import vip.yazilim.p2g.web.model.RoomUserModel;
import vip.yazilim.p2g.web.repository.IRoomUserRepo;
import vip.yazilim.p2g.web.service.p2g.IRoomInviteService;
import vip.yazilim.p2g.web.service.p2g.IRoomService;
import vip.yazilim.p2g.web.service.p2g.IRoomUserService;
import vip.yazilim.p2g.web.service.p2g.IUserService;
import vip.yazilim.p2g.web.service.spotify.impl.SpotifyPlayerService;
import vip.yazilim.p2g.web.util.SecurityHelper;
import vip.yazilim.p2g.web.util.TimeHelper;

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

        try {
            userService.getById(entity.getUserId());
            Optional<User> userOpt = userService.getById(entity.getUserId());
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                entity.setUserName(user.getName());
            }
        } catch (Exception ignored) {
            entity.setUserName("UNKNOWN");
        }

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
            throw new ResourceNotFoundException(msg);
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
     * @throws BusinessLogicException BusinessLogicException
     * @throws IOException            IOException
     * @throws SpotifyWebApiException SpotifyWebApiException
     */
    @Override
    public RoomUser joinRoom(Long roomId, String userId, String password, Role role) throws BusinessLogicException, IOException, SpotifyWebApiException {
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
            webSocketController.sendInfoToRoom(roomId, joinedUser.getUserName() + " joined room!");

            LOGGER.info("User[{}] joined Room[{}]", userId, roomId);
            return joinedUser;
        }
    }

    @Override
    public RoomUser joinRoomOwner(Long roomId, String userId) throws BusinessLogicException {
        RoomUser roomUser = new RoomUser();

        roomUser.setRoomId(roomId);
        roomUser.setUserId(userId);
        roomUser.setRole(Role.ROOM_OWNER.getRole());
        roomUser.setActiveFlag(true);

        return super.create(roomUser);
    }

    @Override
    public boolean leaveRoom() throws DatabaseException, InvalidArgumentException {
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
                    Long roomId = roomUser.getRoomId();
                    LOGGER.info("User[{}] leaved Room[{}]", userId, roomId);
                    webSocketController.sendInfoToRoom(roomId, roomUser.getUserName() + " leaved room.");
                }

                try {
                    spotifyPlayerService.userDeSyncWithRoom(roomUser);
                } catch (IOException | SpotifyWebApiException e) {
                    LOGGER.info("An error occurred when User[{}] desync with Room[{}]", userId, roomUser.getRoomId());
                }

                return status;
            }
        } else {
            throw new ResourceNotFoundException("User not in any room");
        }
    }

    @Override
    public List<RoomUserModel> getRoomUserModelsByRoomId(Long roomId) throws DatabaseException, InvalidArgumentException {
        List<RoomUserModel> roomUserModels = new LinkedList<>();
        List<RoomUser> roomUsers = getRoomUsersByRoomId(roomId);

        for (RoomUser ru : roomUsers) {
            RoomUserModel roomUserModel = new RoomUserModel();
            roomUserModel.setRoomUser(ru);
            Optional<User> roomUserOpt = userService.getById(ru.getUserId());
            roomUserOpt.ifPresent(roomUserModel::setUser);
            roomUserModels.add(roomUserModel);
        }

        roomUserModels.sort((o1, o2) -> {
            if (o1.getRoomUser().getRole().equals(Role.ROOM_OWNER.role)) {
                return 3;
            } else if (o1.getRoomUser().getRole().equals(Role.ROOM_ADMIN.role)) {
                return 2;
            } else if (o1.getRoomUser().getRole().equals(Role.ROOM_MODERATOR.role)) {
                return 1;
            } else if (o1.getRoomUser().getRole().equals(Role.ROOM_USER.role)) {
                return 0;
            } else {
                return o1.getRoomUser().getRole().compareTo(o2.getRoomUser().getRole());
            }
        });

        return roomUserModels;
    }

    @Override
    public RoomUser acceptRoomInvite(RoomInvite roomInvite) throws BusinessLogicException {
        if (!roomInviteService.existsById(roomInvite.getId())) {
            String err = String.format("Room Invite[%s] not found", roomInvite.getId());
            throw new ResourceNotFoundException(err);
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
    public RoomUser changeRoomUserRole(Long roomUserId, boolean promoteDemoteFlag) throws DatabaseException, InvalidArgumentException {
        RoomUser roomUser = getSafeRoomUser(roomUserId);

        if (roomUser.getUserId().equals(SecurityHelper.getUserId())) {
            throw new ConstraintViolationException("You can not change your own role.");
        }

        Role oldRole = Role.getRole(roomUser.getRole());
        Role newRole = getNewRole(oldRole, promoteDemoteFlag);

        if (oldRole.equals(newRole)) {
            return roomUser;
        } else {
            roomUser.setRole(newRole.role);
            RoomUser updatedRoomUser = update(roomUser);

            String operation = (promoteDemoteFlag) ? " promoted " : " demoted ";
            String userName = SecurityHelper.getUserDisplayName();
            String infoMessage = userName + operation + roomUser.getUserName() + "'s role to " + newRole.role;
            webSocketController.sendInfoToRoom(roomUser.getRoomId(), infoMessage);

            return updatedRoomUser;
        }
    }

    @Override
    public boolean hasRoomPrivilege(String userId, Privilege privilege) throws DatabaseException {
        Optional<RoomUser> roomUserOpt = getRoomUser(userId);
        return roomUserOpt.isPresent() && authorityProvider.hasPrivilege(roomUserOpt.get().getRole(), privilege);
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
            throw new ResourceNotFoundException("Room user not found");
        }
    }

    private void updateRoomUsers(RoomUser roomUser) throws DatabaseException, InvalidArgumentException {
        Long roomId = roomUser.getRoomId();
        List<RoomUserModel> roomUserModels = getRoomUserModelsByRoomId(roomId);
        roomUserModels.removeIf(roomUserModel -> roomUserModel.getRoomUser() == roomUser);
        webSocketController.sendToRoom("users", roomId, roomUserModels);
    }

    private Role getNewRole(Role oldRole, boolean promoteFlag) {
        switch (oldRole) {
            case ROOM_USER:
                if (promoteFlag)
                    return Role.ROOM_MODERATOR;
                else
                    return Role.ROOM_USER;
            case ROOM_MODERATOR:
                if (promoteFlag)
                    return Role.ROOM_ADMIN;
                else
                    return Role.ROOM_USER;
            case ROOM_ADMIN:
                if (promoteFlag)
                    return Role.ROOM_ADMIN;
                else
                    return Role.ROOM_MODERATOR;
            default:
                return oldRole;
        }
    }
}
