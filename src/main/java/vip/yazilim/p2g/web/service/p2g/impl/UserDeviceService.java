package vip.yazilim.p2g.web.service.p2g.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.entity.UserDevice;
import vip.yazilim.p2g.web.repository.IUserDeviceRepo;
import vip.yazilim.p2g.web.service.p2g.IUserDeviceService;
import vip.yazilim.p2g.web.service.p2g.IUserService;
import vip.yazilim.spring.core.exception.general.InvalidArgumentException;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;
import vip.yazilim.spring.core.exception.general.database.DatabaseReadException;
import vip.yazilim.spring.core.service.ACrudServiceImpl;

import javax.transaction.Transactional;
import java.util.LinkedList;
import java.util.List;

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

    @Override
    public List<UserDevice> getUserDevicesByUserId(String userId) throws DatabaseException {
        List<UserDevice> userDeviceList;

        try {
            userDeviceList = userDeviceRepo.findByUserIdOrderByActiveFlagDesc(userId);
        } catch (Exception exception) {
            String errorMessage = String.format("An error occurred while getting UserDevice with userId[%s]", userId);
            throw new DatabaseReadException(errorMessage, exception);
        }

        return userDeviceList;
    }

    @Override
    public List<UserDevice> getUserDevicesByroomId(Long roomId) throws DatabaseException, InvalidArgumentException {
        List<UserDevice> userDeviceList = new LinkedList<>();
        List<User> userList = userService.getUsersByRoomId(roomId);

        for (User u : userList) {
            List<UserDevice> userDevices = getUserDevicesByUserId(u.getId());
            if (!userDevices.isEmpty())
                userDeviceList.add(userDevices.get(0));
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

}
