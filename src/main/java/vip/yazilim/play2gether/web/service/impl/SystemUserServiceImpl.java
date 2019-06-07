package vip.yazilim.play2gether.web.service.impl;

import vip.yazilim.play2gether.web.entity.SystemUser;
import vip.yazilim.play2gether.web.repository.ISystemUserRepo;
import vip.yazilim.play2gether.web.service.ISystemUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Emre Sen - 24.05.2019
 * @contact maemresen07@gmail.com
 */
@Service
public class SystemUserServiceImpl implements ISystemUserService {

    @Autowired
    private ISystemUserRepo systemUserRepo;

    @Override
    public Optional<SystemUser> getUserByEmail(String email) {
        return systemUserRepo.findByEmail(email);
    }

    @Override
    public Optional<SystemUser> save(SystemUser item) {
        return Optional.of(systemUserRepo.save(item));
    }

    @Override
    public Optional<SystemUser> getByUuid(String uuid) {
        return systemUserRepo.findById(uuid);
    }
}
