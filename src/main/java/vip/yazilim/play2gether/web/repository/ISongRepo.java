package vip.yazilim.play2gether.web.repository;

import org.springframework.data.repository.CrudRepository;
import vip.yazilim.play2gether.web.entity.Song;

/**
 * @author Emre Sen - 21.05.2019
 * @contact maemresen07@gmail.com
 */
public interface ISongRepo extends CrudRepository<Song, String> {

}
