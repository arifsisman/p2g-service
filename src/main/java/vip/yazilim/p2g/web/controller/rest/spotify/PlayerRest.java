package vip.yazilim.p2g.web.controller.rest.spotify;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vip.yazilim.p2g.web.entity.relation.Song;
import vip.yazilim.p2g.web.service.spotify.ISpotifyPlayerService;
import vip.yazilim.spring.core.exception.web.ServiceException;
import vip.yazilim.spring.core.rest.model.RestResponse;
import vip.yazilim.spring.core.util.RestResponseFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    @PostMapping("/play")
    public RestResponse<List<Song>> playSong(HttpServletRequest request, HttpServletResponse response, @RequestBody Song song) {
        List<Song> songList;

        try {
            songList = spotifyPlayerService.play(song, 0);
        } catch (Exception e) {
            throw new ServiceException(e);
        }

        return RestResponseFactory.generateResponse(songList, HttpStatus.OK, request, response);
    }

    @PostMapping("/{roomUuid}/play")
    public RestResponse<List<Song>> startResume(HttpServletRequest request, HttpServletResponse response, @PathVariable String roomUuid) {
        List<Song> songList;

        try {
            songList = spotifyPlayerService.startResume(roomUuid);
        } catch (Exception e) {
            throw new ServiceException(e);
        }

        return RestResponseFactory.generateResponse(songList, HttpStatus.OK, request, response);
    }

    @PostMapping("/{roomUuid}/pause")
    public RestResponse<List<Song>> pause(HttpServletRequest request, HttpServletResponse response, @PathVariable String roomUuid) {
        List<Song> songList;

        try {
            songList = spotifyPlayerService.pause(roomUuid);
        } catch (Exception e) {
            throw new ServiceException(e);
        }

        return RestResponseFactory.generateResponse(songList, HttpStatus.OK, request, response);
    }

    @PostMapping("/{roomUuid}/next")
    public RestResponse<List<Song>> next(HttpServletRequest request, HttpServletResponse response, @PathVariable String roomUuid) {
        // if room in queue mode, delete previous
        // else, keep previous
        // slm
        List<Song> songList;

        try {
            songList = spotifyPlayerService.next(roomUuid);
        } catch (Exception e) {
            throw new ServiceException(e);
        }

        return RestResponseFactory.generateResponse(songList, HttpStatus.OK, request, response);
    }

    @PostMapping("/{roomUuid}/previous")
    public RestResponse<List<Song>> previous(HttpServletRequest request, HttpServletResponse response, @PathVariable String roomUuid) {
        List<Song> songList;

        try {
            songList = spotifyPlayerService.previous(roomUuid);
        } catch (Exception e) {
            throw new ServiceException(e);
        }

        return RestResponseFactory.generateResponse(songList, HttpStatus.OK, request, response);
    }

    @PostMapping("/{roomUuid}/seek/{ms}")
    public RestResponse<Integer> seek(HttpServletRequest request, HttpServletResponse response, @PathVariable String roomUuid, @PathVariable Integer ms) {
        int currentMs;

        try {
            currentMs = spotifyPlayerService.seek(roomUuid, ms);
        } catch (Exception e) {
            throw new ServiceException(e);
        }

        return RestResponseFactory.generateResponse(currentMs, HttpStatus.OK, request, response);
    }

    @PostMapping("/{roomUuid}/repeat")
    public RestResponse<Boolean> repeat(HttpServletRequest request, HttpServletResponse response, @PathVariable String roomUuid) {
        boolean repeat;

        try {
            repeat = spotifyPlayerService.repeat(roomUuid);
        } catch (Exception e) {
            throw new ServiceException(e);
        }

        return RestResponseFactory.generateResponse(repeat, HttpStatus.OK, request, response);
    }
}
