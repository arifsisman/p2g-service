package vip.yazilim.play2gether.web.service.old.impl;

import vip.yazilim.play2gether.web.entity.old.Manager;
import vip.yazilim.play2gether.web.repository.old.IManagerRepo;
import vip.yazilim.play2gether.web.service.old.IManagerServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Emre Sen - 24.05.2019
 * @contact maemresen07@gmail.com
 */
@Service
public class ManagerServiceImplI implements IManagerServiceI {


    @Autowired
    private IManagerRepo managerRepo;

    @Override
    public Optional<Manager> getManagerBySystemUser(String userUuid) {
        return managerRepo.findBySystemUser_uuid(userUuid);
    }

    @Override
    public Optional<Manager> save(Manager item) {
        return Optional.of(managerRepo.save(item));
    }

    @Override
    public Optional<Manager> getByUuid(String uuid) {
        return managerRepo.findById(uuid);
    }

}
