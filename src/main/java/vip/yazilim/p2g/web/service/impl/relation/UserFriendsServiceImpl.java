package vip.yazilim.p2g.web.service.impl.relation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.relation.UserFriends;
import vip.yazilim.p2g.web.repository.relation.IUserFriendsRepo;
import vip.yazilim.p2g.web.service.IUserFriendsService;
import vip.yazilim.spring.utils.service.ACrudServiceImpl;

import java.util.List;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class UserFriendsServiceImpl extends ACrudServiceImpl<UserFriends, String> implements IUserFriendsService {

    // static fields
    private Logger LOGGER = LoggerFactory.getLogger(UserFriendsServiceImpl.class);

    // injected dependencies
    @Autowired
    private IUserFriendsRepo userFriendsRepo;

    @Override
    public List<UserFriends> getUserFriendsByUserUuid(String userUuid) {
        //TODO: implement!!!
        return null;
    }

    @Override
    protected JpaRepository<UserFriends, String> getRepository() {
        return userFriendsRepo;
    }

    @Override
    protected String getId(UserFriends entity) {
        return entity.getUuid();
    }

}
