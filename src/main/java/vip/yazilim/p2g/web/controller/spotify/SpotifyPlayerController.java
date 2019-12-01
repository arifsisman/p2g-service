package vip.yazilim.p2g.web.controller.spotify;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
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

    @GetMapping("/room/{roomUuid}/play/{uri}")
    public boolean play(@PathVariable String roomUuid, @PathVariable String uri) {
        try {
            spotifyPlayerService.play(roomUuid, uri);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return true;
    }

    @GetMapping("/room/{roomUuid}/play")
    public boolean play(@PathVariable String roomUuid) {
        try {
            spotifyPlayerService.play(roomUuid);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return true;
    }

    @GetMapping("/room/{roomUuid}/pause")
    public boolean pause(@PathVariable String roomUuid) {
        try {
            spotifyPlayerService.pause(roomUuid);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return true;
    }

    @GetMapping("/room/{roomUuid}/next")
    public boolean next(@PathVariable String roomUuid) {
        try {
            spotifyPlayerService.next(roomUuid);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return true;
    }

    @GetMapping("/room/{roomUuid}/prev")
    public boolean previous(@PathVariable String roomUuid) {
        try {
            spotifyPlayerService.previous(roomUuid);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return true;
    }

    @GetMapping("/room/{roomUuid}/seek/{ms}")
    public boolean seek(@PathVariable String roomUuid, @PathVariable Integer ms) {
        try {
            spotifyPlayerService.seek(roomUuid, ms);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return true;
    }

    @GetMapping("/room/{roomUuid}/repeat")
    public boolean repeat(@PathVariable String roomUuid) {
        try {
            spotifyPlayerService.repeat(roomUuid);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return true;
    }
}
