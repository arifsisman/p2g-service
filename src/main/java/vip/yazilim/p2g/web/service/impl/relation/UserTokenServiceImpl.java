package vip.yazilim.p2g.web.service.impl.relation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.relation.UserToken;
import vip.yazilim.p2g.web.repository.relation.IUserTokenRepo;
import vip.yazilim.p2g.web.service.relation.IUserTokenService;
import vip.yazilim.spring.utils.service.ACrudServiceImpl;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class UserTokenServiceImpl extends ACrudServiceImpl<UserToken, String> implements IUserTokenService {

    // static fields
    private Logger LOGGER = LoggerFactory.getLogger(UserTokenServiceImpl.class);

    // injected dependencies
    @Autowired
    private IUserTokenRepo userTokenRepo;

    @Override
    protected JpaRepository<UserToken, String> getRepository() {
        return userTokenRepo;
    }

    @Override
    protected String getId(UserToken entity) {
        return entity.getUuid();
    }

}
