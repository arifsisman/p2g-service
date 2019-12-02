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

    @GetMapping("/spotify/{queueUuid}/pause")
    public boolean pause(@PathVariable String queueUuid) {
        try {
            RoomQueue queue = roomQueueService.getById(queueUuid).orElseThrow(() -> new PlayerException("Queue not found"));
            spotifyPlayerService.pause(queue);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return true;
    }

    @GetMapping("/spotify/{queueUuid}/resume")
    public boolean resume(@PathVariable String queueUuid) {
        try {
            RoomQueue queue = roomQueueService.getById(queueUuid).orElseThrow(() -> new PlayerException("Queue not found"));
            spotifyPlayerService.resume(queue);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return true;
    }

    @GetMapping("/spotify/{queueUuid}/next")
    public boolean next(@PathVariable String queueUuid) {
        try {
            RoomQueue queue = roomQueueService.getById(queueUuid).orElseThrow(() -> new PlayerException("Queue not found"));
            spotifyPlayerService.next(queue);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return true;
    }

    @GetMapping("/spotify/{queueUuid}/prev")
    public boolean previous(@PathVariable String queueUuid) {
        try {
            RoomQueue queue = roomQueueService.getById(queueUuid).orElseThrow(() -> new PlayerException("Queue not found"));
            spotifyPlayerService.previous(queue);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return true;
    }

    @GetMapping("/spotify/{queueUuid}/resume/{ms}")
    public boolean seek(@PathVariable String queueUuid, @PathVariable Integer ms) {
        try {
            RoomQueue queue = roomQueueService.getById(queueUuid).orElseThrow(() -> new PlayerException("Queue not found"));
            spotifyPlayerService.seek(queue, ms);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return true;
    }

    @GetMapping("/spotify/{queueUuid}/repeat")
    public boolean repeat(@PathVariable String queueUuid) {
        try {
            RoomQueue queue = roomQueueService.getById(queueUuid).orElseThrow(() -> new PlayerException("Queue not found"));
            spotifyPlayerService.repeat(queue);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return true;
    }
}
