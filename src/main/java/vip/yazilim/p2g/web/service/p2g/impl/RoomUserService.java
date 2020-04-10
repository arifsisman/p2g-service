package vip.yazilim.p2g.web.service.p2g.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.libs.springcore.exception.DatabaseReadException;
import vip.yazilim.libs.springcore.service.ACrudServiceImpl;
import vip.yazilim.p2g.web.config.security.PasswordEncoderConfig;
import vip.yazilim.p2g.web.config.security.authority.AAuthorityProvider;
import vip.yazilim.p2g.web.controller.WebSocketController;
import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.entity.RoomInvite;
import vip.yazilim.p2g.web.entity.RoomUser;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.enums.Privilege;
import vip.yazilim.p2g.web.enums.Role;
import vip.yazilim.p2g.web.exception.ConstraintViolationException;
import vip.yazilim.p2g.web.model.RoomUserModel;
import vip.yazilim.p2g.web.repository.IRoomUserRepo;
import vip.yazilim.p2g.web.service.p2g.IRoomInviteService;
import vip.yazilim.p2g.web.service.p2g.IRoomService;
import vip.yazilim.p2g.web.service.p2g.IRoomUserService;
import vip.yazilim.p2g.web.service.p2g.IUserService;
import vip.yazilim.p2g.web.service.spotify.IPlayerService;
import vip.yazilim.p2g.web.util.SecurityHelper;
import vip.yazilim.p2g.web.util.TimeHelper;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
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
    private IPlayerService spotifyPlayerService;

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
    public List<RoomUser> getRoomUsersByRoomId(Long roomId) {
        try {
            return roomUserRepo.findRoomUserByRoomIdOrderById(roomId);
        } catch (Exception exception) {
            throw new DatabaseReadException(getClassOfEntity(), exception, roomId);
        }
    }

    @Override
    public Optional<RoomUser> getRoomUserByUserId(String userId) {
        try {
            return roomUserRepo.findRoomUserByUserId(userId);
        } catch (Exception exception) {
            throw new DatabaseReadException(getClassOfEntity(), exception, userId);
        }
    }

    @Override
    public RoomUserModel getRoomUserModelMe(String userId) {
        Optional<RoomUser> roomUserOpt = getRoomUserByUserId(userId);
        if (roomUserOpt.isPresent()) {
            RoomUserModel roomUserModel = new RoomUserModel();
            roomUserModel.setRoomUser(roomUserOpt.get());
            userService.getById(userId).ifPresent(roomUserModel::setUser);
            return roomUserModel;
        } else {
            String msg = String.format("[%s] :: Not in any room", userId);
            throw new NoSuchElementException(msg);
        }
    }

    @Override
    public Optional<RoomUser> getRoomUserByUserId(Long roomId, String userId) {
        try {
            return roomUserRepo.findRoomUserByRoomIdAndUserId(roomId, userId);
        } catch (Exception exception) {
            throw new DatabaseReadException(getClassOfEntity(), exception, roomId, userId);
        }
    }

    @Override
    public Optional<RoomUser> getRoomOwner(Long roomId) {
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
     */
    @Override
    public RoomUser joinRoom(Long roomId, String userId, String password, Role role) {
        Optional<Room> roomOpt = roomService.getById(roomId);
        if (!roomOpt.isPresent()) {
            String err = String.format("Room[%s] :: Not found", roomId);
            throw new IllegalArgumentException(err);
        } else {
            // Any room exists check
            Optional<RoomUser> existingUserOpt = getRoomUserByUserId(userId);
            if (existingUserOpt.isPresent()) {
                leaveRoom();
            }

            // Normal condition
            Room room = roomOpt.get();
            RoomUser roomUser = new RoomUser();

            if (room.getPassword() == null || passwordEncoderConfig.passwordEncoder().matches(password, room.getPassword())) {
                roomUser.setRoomId(roomId);
                roomUser.setUserId(userId);
                roomUser.setRole(role.getRole());
                roomUser.setActiveFlag(true);
            } else {
                throw new IllegalArgumentException("Wrong password");
            }

            RoomUser joinedUser = create(roomUser);
            spotifyPlayerService.userSyncWithRoom(joinedUser);
            webSocketController.sendInfoToRoom(roomId, joinedUser.getUserName() + " joined room!");

            LOGGER.info("[{}] :: Joined Room[{}]", userId, roomId);
            return joinedUser;
        }
    }

    @Override
    public void joinRoomOwner(Long roomId, String userId) {
        RoomUser roomUser = new RoomUser();

        roomUser.setRoomId(roomId);
        roomUser.setUserId(userId);
        roomUser.setRole(Role.ROOM_OWNER.getRole());
        roomUser.setActiveFlag(true);

        super.create(roomUser);
    }

    @Override
    public boolean leaveRoom() {
        String userId = SecurityHelper.getUserId();
        Optional<RoomUser> roomUserOpt = getRoomUserByUserId(userId);

        if (roomUserOpt.isPresent()) {
            RoomUser roomUser = roomUserOpt.get();
            if (roomUserOpt.get().getRole().equals(Role.ROOM_OWNER.getRole())) {
                boolean status = roomService.deleteById(roomUser.getRoomId());
                if (status) {
                    LOGGER.info("[{}] :: Closed Room[{}]", userId, roomUser.getRoomId());
                }
                return status;
            } else {
                // Update room users before user leaves
                updateRoomUsers(roomUserOpt.get());

                boolean status = delete(roomUser);
                if (status) {
                    Long roomId = roomUser.getRoomId();
                    LOGGER.info("[{}] :: Leaved Room[{}]", userId, roomId);
                    webSocketController.sendInfoToRoom(roomId, roomUser.getUserName() + " leaved room.");
                }

                try {
                    spotifyPlayerService.userDeSyncWithRoom(roomUser);
                } catch (Exception e) {
                    LOGGER.warn("[{}] :: An error occurred when desync with Room[{}]", userId, roomUser.getRoomId());
                }

                return status;
            }
        } else {
            throw new NoSuchElementException("User not in any room");
        }
    }

    @Override
    public List<RoomUserModel> getRoomUserModelsByRoomId(Long roomId) {
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
            } else if (o1.getRoomUser().getRole().equals(Role.ROOM_DJ.role)) {
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
    public RoomUser acceptRoomInvite(RoomInvite roomInvite) {
        if (!roomInviteService.existsById(roomInvite.getId())) {
            String err = String.format("Room Invite[%s] not found", roomInvite.getId());
            throw new NoSuchElementException(err);
        } else {
            // Any room exists check
            Optional<RoomUser> existingUserOpt = getRoomUserByUserId(roomInvite.getReceiverId());
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

            LOGGER.info("[{}] :: Accepted Room[{}] invite from [{}]", roomInvite.getReceiverId(), roomInvite.getRoomId(), roomInvite.getInviterId());
            return createdRoomUser;
        }
    }

    @Override
    public void deleteRoomUsers(Long roomId) {
        List<RoomUser> roomUserList;

        try {
            roomUserList = roomUserRepo.findRoomUserByRoomIdOrderById(roomId);
        } catch (Exception exception) {
            throw new DatabaseReadException(getClassOfEntity(), exception, roomId);
        }

        for (RoomUser roomUser : roomUserList) {
            delete(roomUser);
        }

    }

    @Override
    public RoomUser changeRoomUserRole(Long roomUserId, String roleName) {
        String userId = SecurityHelper.getUserId();
        Optional<RoomUser> changerOpt = getRoomUserByUserId(userId);

        if (!changerOpt.isPresent()) {
            String err = String.format("[%s] :: Not in room", userId);
            throw new ConstraintViolationException(err);
        }

        RoomUser changer = changerOpt.get();
        RoomUser changingUser = getSafeRoomUser(roomUserId);

        if (changingUser.getUserId().equals(SecurityHelper.getUserId())) {
            throw new ConstraintViolationException("You can not change your own role.");
        } else if (changingUser.getRole().equals(Role.ROOM_OWNER.role)) {
            throw new ConstraintViolationException(Role.ROOM_OWNER.role + " role can not changed.");
        } else if (changingUser.getRole().equals(changer.getRole())) {
            throw new ConstraintViolationException("You can not change the role of users who have the same role as you.");
        }

        Role oldRole = Role.getRole(changingUser.getRole());

        if (oldRole.equals(Role.getRole(roleName))) {
            return changingUser;
        } else if (Role.getRole(roleName).equals(Role.ROOM_OWNER)) {
            return changeRoomOwner(roomUserId);
        } else {
            changingUser.setRole(Role.getRole(roleName).getRole());
            RoomUser updatedRoomUser = update(changingUser);

            String userName = SecurityHelper.getUserDisplayName();
            String infoMessage = userName + " changed " + changingUser.getUserName() + "'s role to " + roleName;
            webSocketController.sendInfoToRoom(changingUser.getRoomId(), infoMessage);

            return updatedRoomUser;
        }
    }

    @Override
    public RoomUser changeRoomOwner(Long roomUserId) {
        Optional<RoomUser> oldRoomOwnerOpt = getRoomUserByUserId(SecurityHelper.getUserId());
        Optional<RoomUser> newRoomOwnerOpt = getById(roomUserId);

        if (newRoomOwnerOpt.isPresent() && oldRoomOwnerOpt.isPresent()) {
            RoomUser oldRoomOwner = oldRoomOwnerOpt.get();
            RoomUser newRoomOwner = newRoomOwnerOpt.get();
            Long roomId = oldRoomOwner.getRoomId();
            Optional<Room> roomOpt = roomService.getById(roomId);
            if (roomOpt.isPresent()) {
                Room room = roomOpt.get();

                oldRoomOwner.setRole(Role.ROOM_ADMIN.getRole());
                newRoomOwner.setRole(Role.ROOM_OWNER.getRole());
                room.setOwnerId(newRoomOwner.getUserId());

                update(oldRoomOwner);
                update(newRoomOwner);
                roomService.update(room);

                String userName = SecurityHelper.getUserDisplayName();
                String infoMessage = userName + "promoted" + newRoomOwner.getUserName() + "'s role to " + Role.ROOM_OWNER.role;
                webSocketController.sendInfoToRoom(newRoomOwner.getRoomId(), infoMessage);

                return newRoomOwner;
            } else {
                String err = String.format("Room[%s] :: Not Found", roomId);
                throw new NoSuchElementException(err);
            }
        } else {
            String err = String.format("RoomUser[%s] :: Not Found", roomUserId);
            throw new NoSuchElementException(err);
        }
    }

    @Override
    public boolean hasRoomPrivilege(String userId, Privilege privilege) {
        Optional<RoomUser> roomUserOpt = getRoomUserByUserId(userId);
        return roomUserOpt.isPresent() && authorityProvider.hasPrivilege(roomUserOpt.get().getRole(), privilege);
    }

    @Override
    public boolean hasRoomRole(String userId, Role role) {
        Optional<RoomUser> roomUserOpt = getRoomUserByUserId(userId);
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

    private RoomUser getSafeRoomUser(Long roomUserId) {
        Optional<RoomUser> roomUserOpt = getById(roomUserId);

        if (roomUserOpt.isPresent()) {
            return roomUserOpt.get();
        } else {
            throw new NoSuchElementException("Room user not found");
        }
    }

    private void updateRoomUsers(RoomUser roomUser) {
        Long roomId = roomUser.getRoomId();
        List<RoomUserModel> roomUserModels = getRoomUserModelsByRoomId(roomId);
        roomUserModels.removeIf(roomUserModel -> roomUserModel.getRoomUser() == roomUser);
        webSocketController.sendToRoom("users", roomId, roomUserModels);
    }

    private Role getNewRole(Role oldRole, boolean promoteFlag) {
        switch (oldRole) {
            case ROOM_USER:
                if (promoteFlag)
                    return Role.ROOM_DJ;
                else
                    return Role.ROOM_USER;
            case ROOM_DJ:
                if (promoteFlag)
                    return Role.ROOM_ADMIN;
                else
                    return Role.ROOM_USER;
            case ROOM_ADMIN:
                if (promoteFlag)
                    return Role.ROOM_ADMIN;
                else
                    return Role.ROOM_DJ;
            default:
                return oldRole;
        }
    }
}
