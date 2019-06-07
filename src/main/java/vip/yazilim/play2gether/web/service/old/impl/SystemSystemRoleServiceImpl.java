package vip.yazilim.play2gether.web.service.old.impl;

import vip.yazilim.play2gether.web.entity.old.SystemRole;
import vip.yazilim.play2gether.web.repository.ISystemRoleRepo;
import vip.yazilim.play2gether.web.service.old.ISystemRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Emre Sen - 24.05.2019
 * @contact maemresen07@gmail.com
 */
@Service
public class SystemSystemRoleServiceImpl implements ISystemRoleService {

    @Autowired
    private ISystemRoleRepo systemRoleRepo;

    @Override
    public Optional<SystemRole> save(SystemRole item) {
        return Optional.of(systemRoleRepo.save(item));
    }

    @Override
    public Optional<SystemRole> getByUuid(String uuid) {
        return systemRoleRepo.findById(uuid);
    }
}
