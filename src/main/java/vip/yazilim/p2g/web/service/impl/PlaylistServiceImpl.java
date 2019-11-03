package vip.yazilim.p2g.web.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.Playlist;
import vip.yazilim.p2g.web.repository.IPlaylistRepo;
import vip.yazilim.p2g.web.service.IPlaylistService;
import vip.yazilim.spring.utils.exception.DatabaseException;
import vip.yazilim.spring.utils.service.ACrudServiceImpl;

import java.util.Optional;

/**
 * @author mustafaarifsisman - 1.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class PlaylistServiceImpl extends ACrudServiceImpl<Playlist, String> implements IPlaylistService {

    // static fields
    private Logger LOGGER = LoggerFactory.getLogger(PlaylistServiceImpl.class);

    // injected dependencies
    @Autowired
    private IPlaylistRepo playlistRepo;

    @Override
    public Optional<String> getImageUrlByPlaylistUuid(String playlistUuid) throws DatabaseException {
        Optional<String> imageUrl;

        try {
            Playlist playlist = playlistRepo.findByUuid(playlistUuid).get();
            imageUrl = Optional.of(playlist.getImageUrl());
        } catch (Exception exception) {
            String errorMessage = String.format("An error occurred while getting Image URL with playlistUuid[%s]", playlistUuid);
            throw new DatabaseException(errorMessage, exception);
        }

        return imageUrl;
    }

    @Override
    protected JpaRepository<Playlist, String> getRepository() {
        return playlistRepo;
    }

    @Override
    protected String getId(Playlist entity) {
        return entity.getUuid();
    }
}
