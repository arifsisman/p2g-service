package vip.yazilim.p2g.web.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.p2g.web.service.p2g.ISongService;

import java.util.LinkedList;
import java.util.List;

/**
 * @author mustafaarifsisman - 09.04.2020
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class ActiveSongsProvider implements IActiveSongsProvider {

    @Autowired
    private ISongService songService;

    private List<Song> activeSongs = new LinkedList<>();

    private Logger LOGGER = LoggerFactory.getLogger(ActiveSongsProvider.class);

    @Override
    public List<Song> getActiveSongs() {
        return activeSongs;
    }

    @Override
    public void activateSong(Song song) {
        if (!activeSongs.contains(song)) {
            activeSongs.add(song);
            LOGGER.info("Song[{}] :: Activated", song.getId());
        }
    }

    @Override
    public void deactivateSong(Song song) {
        activeSongs.remove(song);
        LOGGER.info("Song[{}] :: Deactivated", song.getId());
    }


    @EventListener(ApplicationReadyEvent.class)
    public void activeExistingPlayingSongsOnApplicationReadyEvent() {
        for (Song song : songService.getActiveSongs()) {
            activateSong(song);
        }
    }
}
