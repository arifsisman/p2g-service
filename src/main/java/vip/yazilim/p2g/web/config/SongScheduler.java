package vip.yazilim.p2g.web.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vip.yazilim.p2g.web.controller.WebSocketController;
import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.p2g.web.service.IActiveSongsProvider;
import vip.yazilim.p2g.web.service.p2g.ISongService;
import vip.yazilim.p2g.web.service.spotify.IPlayerService;
import vip.yazilim.p2g.web.util.TimeHelper;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 09.04.2020
 * @contact mustafaarifsisman@gmail.com
 */
@EnableScheduling
@Component
@Transactional
public class SongScheduler {

    private Logger LOGGER = LoggerFactory.getLogger(SongScheduler.class);

    @Autowired
    private IActiveSongsProvider activeRoomsProvider;

    @Autowired
    private ISongService songService;

    @Autowired
    private IPlayerService spotifyPlayerService;

    @Autowired
    private WebSocketController webSocketController;

    @Scheduled(fixedDelay = 1000, initialDelay = 10000)
    public void checkRoomSongFinished() {
        List<Song> activeSongs = activeRoomsProvider.getActiveSongs();

        for (Song song : activeSongs) {
            if (isSongFinished(song)) {
                Optional<Song> nextOpt = songService.getNextSong(song.getRoomId());
                if (!nextOpt.isPresent()) {
                    activeRoomsProvider.deactivateSong(song);
                } else {
                    Long roomId = song.getRoomId();
                    LOGGER.info("Room[{}] :: Song finished, next song will be played.", roomId);
                    spotifyPlayerService.roomNext(roomId);
                    webSocketController.sendToRoom("songs", roomId, songService.getSongListByRoomId(roomId));
                }
            }
        }
    }

    private boolean isSongFinished(Song song) {
        return song.getDurationMs() - (ChronoUnit.MILLIS.between(song.getPlayingTime(), TimeHelper.getLocalDateTimeNow()) + song.getCurrentMs()) < 1000;
    }
}
