package vip.yazilim.play2gether.web.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import vip.yazilim.play2gether.web.entity.SystemRole;
import vip.yazilim.play2gether.web.entity.SystemUser;
import vip.yazilim.play2gether.web.repository.ISystemRoleRepo;
import vip.yazilim.play2gether.web.repository.ISystemUserRepo;
import vip.yazilim.play2gether.web.service.ISystemRoleService;
import vip.yazilim.play2gether.web.service.ISystemUserService;

import java.util.Optional;

@Service
public class SystemUserServiceImpl implements ISystemUserService {

    @Autowired
    private ISystemUserRepo systemUserRepo;

    @Override
    public Optional<SystemUser> save(SystemUser item) {
        return Optional.of(systemUserRepo.save(item));
    }

    @Override
    public Optional<SystemUser> getByUuid(String uuid) {
        return systemUserRepo.findById(uuid);
    }

    @Override
    public Optional<SystemUser> getUserByEmail(String email) {
        return systemUserRepo.findByEmail(email);
    }
}
