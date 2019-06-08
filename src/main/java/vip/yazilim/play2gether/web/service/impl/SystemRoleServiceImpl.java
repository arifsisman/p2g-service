package vip.yazilim.play2gether.web.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import vip.yazilim.play2gether.web.entity.Song;
import vip.yazilim.play2gether.web.entity.SystemRole;
import vip.yazilim.play2gether.web.repository.ISongRepo;
import vip.yazilim.play2gether.web.repository.ISystemRoleRepo;
import vip.yazilim.play2gether.web.service.ISongService;
import vip.yazilim.play2gether.web.service.ISystemRoleService;

import java.util.Optional;

@Controller
public class SystemRoleServiceImpl implements ISystemRoleService {

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
