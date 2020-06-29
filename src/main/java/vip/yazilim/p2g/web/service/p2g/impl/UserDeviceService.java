package vip.yazilim.p2g.web.service.p2g.impl;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.libs.springcore.exception.DatabaseReadException;
import vip.yazilim.libs.springcore.service.ACrudServiceImpl;
import vip.yazilim.p2g.web.entity.RoomUser;
import vip.yazilim.p2g.web.entity.UserDevice;
import vip.yazilim.p2g.web.repository.IUserDeviceRepo;
import vip.yazilim.p2g.web.service.p2g.IRoomUserService;
import vip.yazilim.p2g.web.service.p2g.IUserDeviceService;
import vip.yazilim.p2g.web.service.spotify.IPlayerService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyUserService;

import java.util.Optional;

/**
 * @author mustafaarifsisman - 30.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class UserDeviceService extends ACrudServiceImpl<UserDevice, String> implements IUserDeviceService {

    private final IUserDeviceRepo userDeviceRepo;
    private final ISpotifyUserService spotifyUserService;
    private final IPlayerService spotifyPlayerService;
    private final IRoomUserService roomUserService;

    public UserDeviceService(IUserDeviceRepo userDeviceRepo, ISpotifyUserService spotifyUserService, @Lazy IPlayerService spotifyPlayerService, IRoomUserService roomUserService) {
        this.userDeviceRepo = userDeviceRepo;
        this.spotifyUserService = spotifyUserService;
        this.spotifyPlayerService = spotifyPlayerService;
        this.roomUserService = roomUserService;
    }

    @Override
    public Optional<UserDevice> getUsersActiveDevice(String userId) {
        try {
            return userDeviceRepo.findByUserIdAndActiveFlag(userId, true);
        } catch (Exception exception) {
            throw new DatabaseReadException(getClassOfEntity(), exception, userId);
        }
    }

    @Override
    public UserDevice changeUsersActiveDevice(String userId, UserDevice userDevice) {
        Optional<UserDevice> oldUserDeviceOpt;

        try {
            oldUserDeviceOpt = userDeviceRepo.findByUserIdAndActiveFlag(userId, true);
        } catch (Exception exception) {
            throw new DatabaseReadException(getClassOfEntity(), exception, userId);
        }

        if (oldUserDeviceOpt.isPresent()) {
            UserDevice oldUserDevice = oldUserDeviceOpt.get();
            oldUserDevice.setActiveFlag(false);
            update(oldUserDevice);

            userDevice.setActiveFlag(true);
            UserDevice updatedUserDevice = create(userDevice);

            spotifyUserService.transferUsersPlayback(userDevice);

            Optional<RoomUser> roomUserOpt = roomUserService.getRoomUserByUserId(userId);
            roomUserOpt.ifPresent(roomUser -> spotifyPlayerService.syncWithRoom(userId));

            return updatedUserDevice;
        } else {
            userDevice.setActiveFlag(true);
            return create(userDevice);
        }
    }

    @Override
    protected JpaRepository<UserDevice, String> getRepository() {
        return userDeviceRepo;
    }

    @Override
    protected String getId(UserDevice userDevice) {
        return userDevice.getId();
    }

    @Override
    protected Class<UserDevice> getClassOfEntity() {
        return UserDevice.class;
    }

}
