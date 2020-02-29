package vip.yazilim.p2g.web.service.p2g.impl;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.entity.UserDevice;
import vip.yazilim.p2g.web.repository.IUserDeviceRepo;
import vip.yazilim.p2g.web.service.p2g.IUserDeviceService;
import vip.yazilim.p2g.web.service.p2g.IUserService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyUserService;
import vip.yazilim.spring.core.exception.InvalidArgumentException;
import vip.yazilim.spring.core.exception.database.DatabaseException;
import vip.yazilim.spring.core.exception.database.DatabaseReadException;
import vip.yazilim.spring.core.exception.web.NotFoundException;
import vip.yazilim.spring.core.service.ACrudServiceImpl;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 30.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Transactional
@Service
public class UserDeviceService extends ACrudServiceImpl<UserDevice, String> implements IUserDeviceService {

    // injected dependencies
    @Autowired
    private IUserDeviceRepo userDeviceRepo;

    @Autowired
    private IUserService userService;

    @Autowired
    private ISpotifyUserService spotifyUserService;

    @Override
    public Optional<UserDevice> getUsersActiveDevice(String userId) throws DatabaseReadException {
        try {
            return userDeviceRepo.findByUserIdAndActiveFlag(userId, true);
        } catch (Exception exception) {
            throw new DatabaseReadException(getClassOfEntity(), exception, userId);
        }
    }

    @Override
    public boolean setUsersActiveDevice(String userId, UserDevice userDevice) throws DatabaseException, InvalidArgumentException, IOException, SpotifyWebApiException {
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
            update(userDevice);

            return spotifyUserService.transferUsersPlayback(userDevice);
        } else {
            throw new NotFoundException("Active device cannot found");
        }
    }

    @Override
    public List<UserDevice> getUserDevicesByUserId(String userId) throws DatabaseException {
        List<UserDevice> userDeviceList;

        try {
            userDeviceList = userDeviceRepo.findByUserIdOrderByActiveFlagDesc(userId);
        } catch (Exception exception) {
            throw new DatabaseReadException(getClassOfEntity(), exception, userId);
        }

        return userDeviceList;
    }

    @Override
    public List<UserDevice> getUserDevicesByRoomId(Long roomId) throws DatabaseException, InvalidArgumentException {
        List<UserDevice> userDeviceList = new LinkedList<>();
        List<User> userList = userService.getUsersByRoomId(roomId);

        for (User u : userList) {
            Optional<UserDevice> userDeviceOpt = getUsersActiveDevice(u.getId());
            userDeviceOpt.ifPresent(userDeviceList::add);
        }

        return userDeviceList;
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
