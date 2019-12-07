package vip.yazilim.p2g.web.controller.rest.spotify;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vip.yazilim.p2g.web.entity.relation.RoomQueue;
import vip.yazilim.p2g.web.service.spotify.ISpotifyPlayerService;
import vip.yazilim.spring.core.exception.web.ServiceException;

import java.util.List;

import static vip.yazilim.p2g.web.constant.Constants.API_SPOTIFY;

/**
 * @author mustafaarifsisman - 1.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@RestController
@RequestMapping(API_SPOTIFY + "/player")
public class SpotifyPlayerController {

    @Autowired
    private ISpotifyPlayerService spotifyPlayerService;

    @GetMapping("/{queueUuid}")
    public List<RoomQueue> play(@PathVariable String queueUuid) {
        try {
            return spotifyPlayerService.play(queueUuid);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @GetMapping("/{roomUuid}/pause")
    public List<RoomQueue> pause(@PathVariable String roomUuid) {
        try {
            return spotifyPlayerService.pause(roomUuid);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @GetMapping("/{roomUuid}/resume")
    public List<RoomQueue> resume(@PathVariable String roomUuid) {
        try {
            return spotifyPlayerService.resume(roomUuid);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @GetMapping("/{roomUuid}/next")
    public List<RoomQueue> next(@PathVariable String roomUuid) {
        try {
            return spotifyPlayerService.next(roomUuid);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @GetMapping("/{roomUuid}/prev")
    public List<RoomQueue> previous(@PathVariable String roomUuid) {
        try {
            return spotifyPlayerService.previous(roomUuid);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @GetMapping("/{roomUuid}/seek/{ms}")
    public List<RoomQueue> seek(@PathVariable String roomUuid, @PathVariable Integer ms) {
        try {
            return spotifyPlayerService.seek(roomUuid, ms);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @GetMapping("/{roomUuid}/repeat")
    public List<RoomQueue> repeat(@PathVariable String roomUuid) {
        try {
            return spotifyPlayerService.repeat(roomUuid);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }
}
