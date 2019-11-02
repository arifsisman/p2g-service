package vip.yazilim.p2g.web.service;

import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.p2g.web.exception.DatabaseException;
import vip.yazilim.spring.utils.service.ICrudService;

import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ISongService extends ICrudService<Song, String> {

    Optional<Song> getSongByName(String songName) throws DatabaseException;

}
