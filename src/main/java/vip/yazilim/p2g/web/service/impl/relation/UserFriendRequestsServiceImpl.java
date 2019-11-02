package vip.yazilim.p2g.web.service.impl.relation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.relation.UserFriendRequests;
import vip.yazilim.p2g.web.repository.relation.IUserFriendRequestsRepo;
import vip.yazilim.p2g.web.service.IUserFriendRequestService;
import vip.yazilim.p2g.web.service.impl.ACrudServiceImpl;

import java.util.List;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class UserFriendRequestsServiceImpl extends ACrudServiceImpl<UserFriendRequests, String> implements IUserFriendRequestService {

    // static fields
    private Logger LOGGER = LoggerFactory.getLogger(UserFriendRequestsServiceImpl.class);

    // injected dependencies
    @Autowired
    private IUserFriendRequestsRepo userFriendRequestsRepo;

    @Override
    public List<UserFriendRequests> getUserFriendRequestsByUserUuid(String userUuid) {
        //TODO: implement!!!
        return null;
    }

    @Override
    protected JpaRepository<UserFriendRequests, String> getRepository() {
        return userFriendRequestsRepo;
    }

    @Override
    protected String getId(UserFriendRequests entity) {
        return entity.getUuid();
    }

}
