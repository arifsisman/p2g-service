package vip.yazilim.p2g.web.controller.rest.spotify;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vip.yazilim.p2g.web.entity.relation.RoomQueue;
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
    public RestResponse<List<RoomQueue>> playQueue(HttpServletRequest request, HttpServletResponse response, @RequestBody RoomQueue roomQueue) {
        List<RoomQueue> roomQueueList;

        try {
            roomQueueList = spotifyPlayerService.play(roomQueue);
        } catch (Exception e) {
            throw new ServiceException(e);
        }

        return RestResponseFactory.generateResponse(roomQueueList, HttpStatus.OK, request, response);
    }

    @PostMapping("/{roomUuid}/play")
    public RestResponse<List<RoomQueue>> startResume(HttpServletRequest request, HttpServletResponse response, @PathVariable String roomUuid) {
        List<RoomQueue> roomQueueList;

        try {
            roomQueueList = spotifyPlayerService.startResume(roomUuid);
        } catch (Exception e) {
            throw new ServiceException(e);
        }

        return RestResponseFactory.generateResponse(roomQueueList, HttpStatus.OK, request, response);
    }

    @PostMapping("/{roomUuid}/pause")
    public RestResponse<List<RoomQueue>> pause(HttpServletRequest request, HttpServletResponse response, @PathVariable String roomUuid) {
        List<RoomQueue> roomQueueList;

        try {
            roomQueueList = spotifyPlayerService.pause(roomUuid);
        } catch (Exception e) {
            throw new ServiceException(e);
        }

        return RestResponseFactory.generateResponse(roomQueueList, HttpStatus.OK, request, response);
    }

    @PostMapping("/{roomUuid}/next")
    public RestResponse<List<RoomQueue>> next(HttpServletRequest request, HttpServletResponse response, @PathVariable String roomUuid) {
        List<RoomQueue> roomQueueList;

        try {
            roomQueueList = spotifyPlayerService.next(roomUuid);
        } catch (Exception e) {
            throw new ServiceException(e);
        }

        return RestResponseFactory.generateResponse(roomQueueList, HttpStatus.OK, request, response);
    }

    @PostMapping("/{roomUuid}/previous")
    public RestResponse<List<RoomQueue>> previous(HttpServletRequest request, HttpServletResponse response, @PathVariable String roomUuid) {
        List<RoomQueue> roomQueueList;

        try {
            roomQueueList = spotifyPlayerService.previous(roomUuid);
        } catch (Exception e) {
            throw new ServiceException(e);
        }

        return RestResponseFactory.generateResponse(roomQueueList, HttpStatus.OK, request, response);
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
