package vip.yazilim.p2g.web.service.impl.relation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.relation.RoomUser;
import vip.yazilim.p2g.web.repository.relation.IRoomUserRepo;
import vip.yazilim.p2g.web.service.relation.IRoomUserService;
import vip.yazilim.spring.utils.service.ACrudServiceImpl;

/**
 * @author mustafaarifsisman - 2.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class RoomUserServiceImpl extends ACrudServiceImpl<RoomUser, String> implements IRoomUserService {

    // static fields
    private Logger LOGGER = LoggerFactory.getLogger(RoomUserServiceImpl.class);

    // injected dependencies
    @Autowired
    private IRoomUserRepo roomUserRepo;

    @Override
    protected JpaRepository<RoomUser, String> getRepository() {
        return roomUserRepo;
    }

    @Override
    protected String getId(RoomUser entity) {
        return entity.getUuid();
    }
}
