package vip.yazilim.play2gether.web.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import vip.yazilim.play2gether.web.entity.ListenQueue;
import vip.yazilim.play2gether.web.repository.IListenQueueRepo;
import vip.yazilim.play2gether.web.service.IListenQueueService;

import java.util.Optional;

@Controller
public class ListenQueueServiceImpl implements IListenQueueService {

    @Autowired
    private IListenQueueRepo listenQueueRepo;

    @Override
    public Optional<ListenQueue> save(ListenQueue item) {
        return Optional.of(listenQueueRepo.save(item));
    }

    @Override
    public Optional<ListenQueue> getByUuid(String uuid) {
        return listenQueueRepo.findById(uuid);
    }
}
