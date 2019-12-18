package vip.yazilim.p2g.web.service.p2g.impl.relation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.entity.relation.UserDevice;
import vip.yazilim.p2g.web.repository.relation.IUserDeviceRepo;
import vip.yazilim.p2g.web.service.p2g.IUserService;
import vip.yazilim.p2g.web.service.p2g.relation.IUserDeviceService;
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
    public List<UserDevice> getUserDevicesByUserUuid(String userUuid) throws DatabaseException {
        List<UserDevice> userDeviceList;

        try {
            userDeviceList = userDeviceRepo.findUserDevicesByUserUuidOrderByActiveFlagDesc(userUuid);
        } catch (Exception exception) {
            String errorMessage = String.format("An error occurred while getting UserDevice with userUuid[%s]", userUuid);
            throw new DatabaseReadException(errorMessage, exception);
        }

        return userDeviceList;
    }

    @Override
    public List<UserDevice> getUserDevicesByRoomUuid(String roomUuid) throws DatabaseException, InvalidArgumentException {
        List<UserDevice> userDeviceList = new LinkedList<>();
        List<User> userList = userService.getUsersByRoomUuid(roomUuid);

        for (User u : userList) {
            List<UserDevice> userDevices = getUserDevicesByUserUuid(u.getUuid());
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
        return userDevice.getDeviceId();
    }

}