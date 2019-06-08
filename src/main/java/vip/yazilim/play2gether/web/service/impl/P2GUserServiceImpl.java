package vip.yazilim.play2gether.web.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import vip.yazilim.play2gether.web.entity.P2GToken;
import vip.yazilim.play2gether.web.entity.P2GUser;
import vip.yazilim.play2gether.web.repository.IP2GTokenRepo;
import vip.yazilim.play2gether.web.repository.IP2GUserRepo;
import vip.yazilim.play2gether.web.service.IP2GTokenService;
import vip.yazilim.play2gether.web.service.IP2GUserService;

import java.util.Optional;

@Service
public class P2GUserServiceImpl implements IP2GUserService {

    @Autowired
    private IP2GUserRepo p2gUserRepo;

    @Override
    public Optional<P2GUser> save(P2GUser item) {
        return Optional.of(p2gUserRepo.save(item));
    }

    @Override
    public Optional<P2GUser> getByUuid(String uuid) {
        return p2gUserRepo.findById(uuid);
    }
}
