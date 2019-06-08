package vip.yazilim.play2gether.web.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import vip.yazilim.play2gether.web.entity.ListenSession;
import vip.yazilim.play2gether.web.entity.P2GToken;
import vip.yazilim.play2gether.web.repository.IP2GTokenRepo;
import vip.yazilim.play2gether.web.service.IListenSessionService;
import vip.yazilim.play2gether.web.service.IP2GTokenService;

import java.util.Optional;

@Controller
public class P2GTokenServiceImpl implements IP2GTokenService {

    @Autowired
    private IP2GTokenRepo p2gTokenRepo;

    @Override
    public Optional<P2GToken> save(P2GToken item) {
        return Optional.of(p2gTokenRepo.save(item));
    }

    @Override
    public Optional<P2GToken> getByUuid(String uuid) {
        return p2gTokenRepo.findById(uuid);
    }
}
