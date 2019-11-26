package vip.yazilim.p2g.web.service.impl.relation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.p2g.web.entity.relation.AlbumSong;
import vip.yazilim.p2g.web.repository.relation.IAlbumSongRepo;
import vip.yazilim.p2g.web.service.ISongService;
import vip.yazilim.p2g.web.service.relation.IAlbumSongService;
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
public class AlbumSongServiceImpl extends ACrudServiceImpl<AlbumSong, String> implements IAlbumSongService {

    // static fields
    private Logger LOGGER = LoggerFactory.getLogger(AlbumSongServiceImpl.class);

    // injected dependencies
    @Autowired
    private IAlbumSongRepo albumSongRepo;

    @Autowired
    private ISongService songService;


    @Override
    protected JpaRepository<AlbumSong, String> getRepository() {
        return albumSongRepo;
    }

    @Override
    protected String getId(AlbumSong entity) {
        return entity.getUuid();
    }

    @Override
    public List<AlbumSong> getAlbumSongListByAlbum(String albumUuid) throws DatabaseException {
        //TODO: implement!!!!
        return null;
    }
}
