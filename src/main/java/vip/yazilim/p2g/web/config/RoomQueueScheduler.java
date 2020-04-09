package vip.yazilim.p2g.web.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vip.yazilim.p2g.web.controller.WebSocketController;
import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.p2g.web.service.IActiveRoomsProvider;
import vip.yazilim.p2g.web.service.p2g.ISongService;
import vip.yazilim.p2g.web.service.spotify.impl.SpotifyPlayerService;
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
public class RoomQueueScheduler {

    private Logger LOGGER = LoggerFactory.getLogger(RoomQueueScheduler.class);

    @Autowired
    private IActiveRoomsProvider activeRoomsProvider;

    @Autowired
    private ISongService songService;

    @Autowired
    private SpotifyPlayerService spotifyPlayerService;

    @Autowired
    private WebSocketController webSocketController;

    @Scheduled(fixedDelay = 1000, initialDelay = 10000)
    public void checkRoomSongFinished() {
        List<Room> roomList = activeRoomsProvider.getActiveRooms();

        for (Room room : roomList) {
            Long roomId = room.getId();
            Optional<Song> playingOpt = songService.getPlayingSong(roomId);
            Optional<Song> nextOpt = songService.getNextSong(roomId);

            if (!nextOpt.isPresent()) {
                activeRoomsProvider.deactivateRoom(room);
//                webSocketController.sendToRoom("songs", roomId, new LinkedList<>());
            } else if (playingOpt.isPresent() && isSongFinished(playingOpt.get())) {
                LOGGER.info("Song[{}] finished on Room[{}]. The next song will be played.", playingOpt.get().getSongId(), roomId);
                spotifyPlayerService.roomNext(roomId);
                webSocketController.sendToRoom("songs", roomId, songService.getSongListByRoomId(roomId));
            }
        }
    }

    private boolean isSongFinished(Song song) {
        return song.getDurationMs() - (ChronoUnit.MILLIS.between(song.getPlayingTime(), TimeHelper.getLocalDateTimeNow()) + song.getCurrentMs()) < 1000;
    }
}
