package vip.yazilim.p2g.web.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.Album;
import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.p2g.web.entity.relation.AlbumSong;
import vip.yazilim.p2g.web.repository.IAlbumRepo;
import vip.yazilim.p2g.web.repository.relation.IAlbumSongRepo;
import vip.yazilim.p2g.web.service.IAlbumService;
import vip.yazilim.p2g.web.service.ISongService;
import vip.yazilim.spring.utils.exception.DatabaseException;
import vip.yazilim.spring.utils.service.ACrudServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 31.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class AlbumServiceImpl extends ACrudServiceImpl<Album, String> implements IAlbumService {

    // static fields
    private Logger LOGGER = LoggerFactory.getLogger(AlbumServiceImpl.class);

    // injected dependencies
    @Autowired
    private IAlbumRepo albumRepo;

    @Autowired
    private ISongService songService;

    @Autowired
    private IAlbumSongRepo albumSongRepo;

    @Override
    public List<Song> getSongsByAlbumUuid(String albumUuid) throws DatabaseException {
        String songUuid;
        List<Song> songList;

        songList = new ArrayList<>();
        List<AlbumSong> albumSongList;
        
        try {
            albumSongList = albumSongRepo.findByAlbumUuid(albumUuid);
        } catch (Exception exception) {
            String errorMessage = String.format("An error occurred while getting Album with albumUuid[%s]", albumUuid);
            throw new DatabaseException(errorMessage, exception);
        }

for (AlbumSong albumSong: albumSongList) {
            songUuid = albumSong.getUuid();

            Optional<Song> song = songService.getById(songUuid);
            if(!song.isPresent()){
                String warnMessage = String.format("Song[%s] not found", songUuid);
                LOGGER.warn(warnMessage);

// songList.add(UNDEFINED_SONG)
                //TODO: something happened :) what can I do sometimes...
            }
            songList.add(song.get());
        }
        return songList;
    }

    @Override
    public Optional<String> getImageUrlByAlbumUuid(String albumUuid) throws DatabaseException {
        Optional<String> imageUrl;

        try {
            Album album = albumRepo.findByUuid(albumUuid).get();
            imageUrl = Optional.of(album.getImageUrl());
        } catch (Exception exception) {
            String errorMessage = String.format("An error occurred while getting Image URL with albumUuid[%s]", albumUuid);
            throw new DatabaseException(errorMessage, exception);
        }

        return imageUrl;
    }

    @Override
    protected JpaRepository<Album, String> getRepository() {
        return albumRepo;
    }

    @Override
    protected String getId(Album entity) {
        return entity.getUuid();
    }
}
