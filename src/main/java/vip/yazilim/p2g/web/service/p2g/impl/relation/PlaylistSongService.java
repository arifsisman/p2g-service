package vip.yazilim.p2g.web.service.p2g.impl.relation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.relation.PlaylistSong;
import vip.yazilim.p2g.web.repository.relation.IPlaylistSongRepo;
import vip.yazilim.p2g.web.service.p2g.relation.IPlaylistSongService;
import vip.yazilim.p2g.web.util.DBHelper;
import vip.yazilim.spring.utils.exception.DatabaseException;
import vip.yazilim.spring.utils.service.ACrudServiceImpl;

import java.util.List;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class PlaylistSongService extends ACrudServiceImpl<PlaylistSong, String> implements IPlaylistSongService {

    // static fields
    private Logger LOGGER = LoggerFactory.getLogger(PlaylistSongService.class);

    // injected dependencies
    @Autowired
    private IPlaylistSongRepo playlistSongRepo;

    @Override
    protected JpaRepository<PlaylistSong, String> getRepository() {
        return playlistSongRepo;
    }

    @Override
    protected String getId(PlaylistSong entity) {
        return entity.getUuid();
    }

    @Override
    protected PlaylistSong preInsert(PlaylistSong entity) {
        entity.setUuid(DBHelper.getRandomUuid());
        return entity;
    }

    @Override
    public List<PlaylistSong> getPlaylistSongListByPlaylist(String playlistUuid) throws DatabaseException {
        List<PlaylistSong> playlistSongList;

        try {
            playlistSongList = playlistSongRepo.findByPlaylistUuid(playlistUuid);
        } catch (Exception exception) {
            String errorMessage = String.format("An error occurred while getting PlaylistSongs from playlistUuid[%s]", playlistUuid);
            throw new DatabaseException(errorMessage, exception);
        }

        return playlistSongList;
    }
}
