package vip.yazilim.p2g.web.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.p2g.web.repository.ISongRepo;
import vip.yazilim.p2g.web.service.ISongService;
import vip.yazilim.p2g.web.util.DBHelper;
import vip.yazilim.spring.utils.exception.DatabaseException;
import vip.yazilim.spring.utils.service.ACrudServiceImpl;

import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class SongServiceImpl extends ACrudServiceImpl<Song, String> implements ISongService {

    @Autowired
    private ISongRepo songRepo;

    @Override
    protected JpaRepository<Song, String> getRepository() {
        return songRepo;
    }

    @Override
    protected String getId(Song entity) {
        return entity.getUuid();
    }

    @Override
    public Optional<Song> getSongByName(String songName) throws DatabaseException {
        Optional<Song> songOptional;

        try {
            songOptional = songRepo.findBySongName(songName);
        } catch (Exception exception) {
            String errorMessage = String.format("An error occurred while getting Song with name[%s]", songName);
            throw new DatabaseException(errorMessage, exception);
        }

        return songOptional;
    }

    // private static final UndefinedSong x = new UndinfedSong();
    // return x;
    @Override
    public Song getUndefinedSong() {
        Song undefinedSong = new Song();

        undefinedSong.setUuid(DBHelper.getRandomUuid());
        undefinedSong.setName("Undefined Song");

        return undefinedSong;
    }

}
