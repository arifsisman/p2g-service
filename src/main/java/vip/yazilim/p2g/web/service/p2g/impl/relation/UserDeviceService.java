package vip.yazilim.p2g.web.service.p2g.impl.relation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.entity.relation.UserDevice;
import vip.yazilim.p2g.web.repository.relation.IUserDeviceRepo;
import vip.yazilim.p2g.web.service.p2g.IUserService;
import vip.yazilim.p2g.web.service.p2g.relation.IUserDeviceService;
import vip.yazilim.spring.utils.exception.DatabaseException;
import vip.yazilim.spring.utils.service.ACrudServiceImpl;

import java.util.LinkedList;
import java.util.List;

/**
 * @author mustafaarifsisman - 30.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class UserDeviceService extends ACrudServiceImpl<UserDevice, String> implements IUserDeviceService {

    // static fields
    private Logger LOGGER = LoggerFactory.getLogger(UserDeviceService.class);

    // injected dependencies
    @Autowired
    private IUserDeviceRepo userDeviceRepo;

    @Autowired
    private IUserService userService;

    @Override
    public List<UserDevice> getDevicesByUserUuid(String userUuid) throws DatabaseException {
        List<UserDevice> userDeviceList;

        try {
            userDeviceList = userDeviceRepo.findUserDevicesByUserUuid(userUuid);
        } catch (Exception exception) {
            String errorMessage = String.format("An error occurred while getting UserDevice with userUuid[%s]", userUuid);
            throw new DatabaseException(errorMessage, exception);
        }

        return userDeviceList;
    }

    @Override
    public List<UserDevice> getDevicesByRoomUuid(String roomUuid) throws DatabaseException {
        List<UserDevice> userDeviceList = new LinkedList<>();
        List<User> userList = userService.getUsersByRoomUuid(roomUuid);

        for(User u: userList){
            //TODO: implement check for users other devices, for example if multiple devices exists, look for active one!
            UserDevice userDevice = getDevicesByUserUuid(u.getUuid()).get(0);
            userDeviceList.add(userDevice);
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
