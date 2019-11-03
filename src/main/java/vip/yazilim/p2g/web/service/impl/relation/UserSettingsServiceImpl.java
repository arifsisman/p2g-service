package vip.yazilim.p2g.web.service.impl.relation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.relation.UserSettings;
import vip.yazilim.p2g.web.repository.relation.IUserSettingsRepo;
import vip.yazilim.p2g.web.service.relation.IUserSettingsService;
import vip.yazilim.spring.utils.service.ACrudServiceImpl;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class UserSettingsServiceImpl extends ACrudServiceImpl<UserSettings, String> implements IUserSettingsService {

    // static fields
    private Logger LOGGER = LoggerFactory.getLogger(UserSettingsServiceImpl.class);

    // injected dependencies
    @Autowired
    private IUserSettingsRepo userSettingsRepo;

    @Override
    protected JpaRepository<UserSettings, String> getRepository() {
        return userSettingsRepo;
    }

    @Override
    protected String getId(UserSettings entity) {
        return entity.getUuid();
    }
}
