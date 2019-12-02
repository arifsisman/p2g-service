package vip.yazilim.p2g.web.controller.spotify;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import vip.yazilim.p2g.web.entity.relation.RoomQueue;
import vip.yazilim.p2g.web.exception.PlayerException;
import vip.yazilim.p2g.web.service.p2g.relation.IRoomQueueService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyPlayerService;
import vip.yazilim.spring.utils.exception.runtime.ServiceException;

/**
 * @author mustafaarifsisman - 1.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@RestController
public class SpotifyPlayerController {

    @Autowired
    private ISpotifyPlayerService spotifyPlayerService;

    @Autowired
    private IRoomQueueService roomQueueService;

    @GetMapping("/spotify/{queueUuid}/play")
    public boolean play(@PathVariable String queueUuid) {
        try {
            RoomQueue queue = roomQueueService.getById(queueUuid).orElseThrow(() -> new PlayerException("Queue not found"));
            spotifyPlayerService.play(queue);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return true;
    }

    @GetMapping("/spotify/{roomUuid}/resume")
    public boolean resume(@PathVariable String roomUuid) {
        try {
            spotifyPlayerService.resume(roomUuid);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return true;
    }

    @GetMapping("/spotify/{roomUuid}/pause")
    public boolean pause(@PathVariable String roomUuid) {
        try {
            spotifyPlayerService.pause(roomUuid);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return true;
    }

    @GetMapping("/spotify/{roomUuid}/next")
    public boolean next(@PathVariable String roomUuid) {
        try {
            spotifyPlayerService.next(roomUuid);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return true;
    }

    @GetMapping("/spotify/{roomUuid}/prev")
    public boolean previous(@PathVariable String roomUuid) {
        try {
            spotifyPlayerService.previous(roomUuid);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return true;
    }

    @GetMapping("/spotify/{roomUuid}/resume/{ms}")
    public boolean seek(@PathVariable String roomUuid, @PathVariable Integer ms) {
        try {
            spotifyPlayerService.seek(roomUuid, ms);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return true;
    }

    @GetMapping("/spotify/{roomUuid}/repeat")
    public boolean repeat(@PathVariable String roomUuid) {
        try {
            spotifyPlayerService.repeat(roomUuid);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return true;
    }
}
