package vip.yazilim.p2g.web.service.impl.relation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.p2g.web.entity.relation.AlbumSong;
import vip.yazilim.p2g.web.entity.relation.PlaylistSong;
import vip.yazilim.p2g.web.repository.relation.IPlaylistSongRepo;
import vip.yazilim.p2g.web.service.ISongService;
import vip.yazilim.p2g.web.service.relation.IPlaylistSongService;
import vip.yazilim.spring.utils.exception.DatabaseException;
import vip.yazilim.spring.utils.service.ACrudServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class PlaylistSongServiceImpl extends ACrudServiceImpl<PlaylistSong, String> implements IPlaylistSongService {

    // static fields
    private Logger LOGGER = LoggerFactory.getLogger(PlaylistSongServiceImpl.class);

    // injected dependencies
    @Autowired
    private IPlaylistSongRepo playlistSongRepo;

    @Autowired
    private ISongService songService;

    @Override
    protected JpaRepository<PlaylistSong, String> getRepository() {
        return playlistSongRepo;
    }

    @Override
    protected String getId(PlaylistSong entity) {
        return entity.getUuid();
    }

    @Override
    public List<PlaylistSong> getPlaylistSongListByPlaylist(String playlistUuid) throws DatabaseException {
        //TODO: implement!!!
        return null;
    }
}
