package vip.yazilim.play2gether.web.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import vip.yazilim.play2gether.web.entity.ListenQueue;
import vip.yazilim.play2gether.web.entity.ListenSession;
import vip.yazilim.play2gether.web.repository.IListenQueueRepo;
import vip.yazilim.play2gether.web.repository.IListenSessionRepo;
import vip.yazilim.play2gether.web.service.IListenQueueService;
import vip.yazilim.play2gether.web.service.IListenSessionService;

import java.util.Optional;

@Controller
public class ListenSessionServiceImpl implements IListenSessionService {

    @Autowired
    private IListenSessionRepo listenSessionRepo;

    @Override
    public Optional<ListenSession> save(ListenSession item) {
        return Optional.of(listenSessionRepo.save(item));
    }

    @Override
    public Optional<ListenSession> getByUuid(String uuid) {
        return listenSessionRepo.findById(uuid);
    }
}
