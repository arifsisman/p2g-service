package vip.yazilim.p2g.web.service.p2g.impl;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.libs.springcore.exception.general.BusinessLogicException;
import vip.yazilim.libs.springcore.exception.general.database.DatabaseReadException;
import vip.yazilim.libs.springcore.service.ACrudServiceImpl;
import vip.yazilim.p2g.web.entity.RoomUser;
import vip.yazilim.p2g.web.entity.UserDevice;
import vip.yazilim.p2g.web.repository.IUserDeviceRepo;
import vip.yazilim.p2g.web.service.p2g.IRoomUserService;
import vip.yazilim.p2g.web.service.p2g.IUserDeviceService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyPlayerService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyUserService;

import java.io.IOException;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 30.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class UserDeviceService extends ACrudServiceImpl<UserDevice, String> implements IUserDeviceService {

    @Autowired
    private IUserDeviceRepo userDeviceRepo;

    @Autowired
    private ISpotifyUserService spotifyUserService;

    @Autowired
    private ISpotifyPlayerService spotifyPlayerService;

    @Autowired
    private IRoomUserService roomUserService;

    @Override
    public Optional<UserDevice> getUsersActiveDevice(String userId) throws DatabaseReadException {
        try {
            return userDeviceRepo.findByUserIdAndActiveFlag(userId, true);
        } catch (Exception exception) {
            throw new DatabaseReadException(getClassOfEntity(), exception, userId);
        }
    }

    @Override
    public UserDevice saveUsersActiveDevice(String userId, UserDevice userDevice) throws BusinessLogicException, IOException, SpotifyWebApiException {
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

            Optional<RoomUser> roomUserOpt = roomUserService.getRoomUser(userId);
            if (roomUserOpt.isPresent()) {
                spotifyPlayerService.userSyncWithRoom(roomUserOpt.get());
            }

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
