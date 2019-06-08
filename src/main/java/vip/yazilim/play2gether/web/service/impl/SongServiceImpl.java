package vip.yazilim.play2gether.web.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import vip.yazilim.play2gether.web.entity.P2GUser;
import vip.yazilim.play2gether.web.entity.Song;
import vip.yazilim.play2gether.web.repository.IP2GUserRepo;
import vip.yazilim.play2gether.web.repository.ISongRepo;
import vip.yazilim.play2gether.web.service.IP2GUserService;
import vip.yazilim.play2gether.web.service.ISongService;

import java.util.Optional;

@Service
public class SongServiceImpl implements ISongService {

    @Autowired
    private ISongRepo songRepo;

    @Override
    public Optional<Song> save(Song item) {
        return Optional.of(songRepo.save(item));
    }

    @Override
    public Optional<Song> getByUuid(String uuid) {
        return songRepo.findById(uuid);
    }
}
