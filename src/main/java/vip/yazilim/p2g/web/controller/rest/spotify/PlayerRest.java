package vip.yazilim.p2g.web.controller.rest.spotify;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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
public class PlayerRest {

    @Autowired
    private ISpotifyPlayerService spotifyPlayerService;

    @GetMapping("/{roomUuid}/play")
    public List<RoomQueue> playRoom(@PathVariable String roomUuid) {
        try {
            return spotifyPlayerService.playRoom(roomUuid);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @PutMapping("/play")
    public List<RoomQueue> playQueue(@RequestBody RoomQueue roomQueue) {
        try {
            return spotifyPlayerService.playQueue(roomQueue);
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

    @GetMapping("/{roomUuid}/previous")
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
