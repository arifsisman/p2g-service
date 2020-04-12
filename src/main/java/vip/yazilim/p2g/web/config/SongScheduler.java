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
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.enums.OnlineStatus;
import vip.yazilim.p2g.web.enums.SongStatus;
import vip.yazilim.p2g.web.service.p2g.IRoomService;
import vip.yazilim.p2g.web.service.p2g.ISongService;
import vip.yazilim.p2g.web.service.p2g.IUserService;
import vip.yazilim.p2g.web.service.spotify.IPlayerService;
import vip.yazilim.p2g.web.util.TimeHelper;

import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
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
    private LinkedList<Song> emptySongList = new LinkedList<>();

    @Autowired
    private ISongService songService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IRoomService roomService;

    @Autowired
    private IPlayerService spotifyPlayerService;

    @Autowired
    private WebSocketController webSocketController;

    @Scheduled(fixedDelay = 1000, initialDelay = 10000)
    public void checkRoomSongFinished() {
        List<Song> activeSongs = songService.getActiveSongs();
        for (Song song : activeSongs) {
            if (isSongFinished(song)) {
                Long roomId = song.getRoomId();
                Optional<Song> nextOpt = songService.getNextSong(song.getRoomId());
                if (nextOpt.isPresent()) {
                    LOGGER.info("Room[{}] :: Song[{}] finished, next Song[{}] is playing.", roomId, song.getSongId(), nextOpt.get().getSongId());
                    spotifyPlayerService.roomNext(roomId);
                    webSocketController.sendToRoom("songs", roomId, songService.getSongListByRoomId(roomId));
                } else {
                    Optional<User> userOpt = roomService.getRoomOwner(roomId);

                    if (userOpt.isPresent()) {
                        User owner = userOpt.get();

                        if (owner.getOnlineStatus().equals(OnlineStatus.OFFLINE.getOnlineStatus())) {
                            roomService.deleteById(roomId);
                        } else {
                            LOGGER.info("Room[{}] :: Song[{}] finished, queue is empty.", roomId, song.getSongId());
                            songService.updateSongStatus(song, SongStatus.PLAYED);
                            webSocketController.sendToRoom("songs", roomId, emptySongList);
                        }
                    }
                }
            }
        }
    }

    private boolean isSongFinished(Song song) {
        return song.getDurationMs() - (ChronoUnit.MILLIS.between(song.getPlayingTime(), TimeHelper.getLocalDateTimeNow()) + song.getCurrentMs()) < 1000;
    }
}
