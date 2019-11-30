package vip.yazilim.p2g.web.service.p2g.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.Playlist;
import vip.yazilim.p2g.web.repository.IPlaylistRepo;
import vip.yazilim.p2g.web.service.p2g.IPlaylistService;
import vip.yazilim.p2g.web.util.DBHelper;
import vip.yazilim.spring.utils.exception.DatabaseException;
import vip.yazilim.spring.utils.service.ACrudServiceImpl;

import java.util.Optional;

/**
 * @author mustafaarifsisman - 1.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class PlaylistService extends ACrudServiceImpl<Playlist, String> implements IPlaylistService {

    // static fields
    private Logger LOGGER = LoggerFactory.getLogger(PlaylistService.class);

    // injected dependencies
    @Autowired
    private IPlaylistRepo playlistRepo;

    @Override
    protected JpaRepository<Playlist, String> getRepository() {
        return playlistRepo;
    }

    @Override
    protected String getId(Playlist entity) {
        return entity.getUuid();
    }

    @Override
    protected Playlist preInsert(Playlist entity) {
        entity.setUuid(DBHelper.getRandomUuid());
        return entity;
    }

    @Override
    public Optional<String> getImageUrlByPlaylistUuid(String playlistUuid) throws DatabaseException {
        try {
            Optional<Playlist> playlist = playlistRepo.findByUuid(playlistUuid);

            if(playlist.isPresent())
                return Optional.of(playlist.get().getImageUrl());

        } catch (Exception exception) {
            String errorMessage = String.format("An error occurred while getting Image URL with playlistUuid[%s]", playlistUuid);
            throw new DatabaseException(errorMessage, exception);
        }

        return Optional.empty();
    }
}
