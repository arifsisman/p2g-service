package vip.yazilim.p2g.web.controller.rest.p2g;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vip.yazilim.p2g.web.config.annotation.HasRoomPrivilege;
import vip.yazilim.p2g.web.config.annotation.UpdateRoomSongs;
import vip.yazilim.p2g.web.constant.enums.Privilege;
import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.p2g.web.model.SearchModel;
import vip.yazilim.p2g.web.service.p2g.ISongService;
import vip.yazilim.spring.core.exception.general.InvalidArgumentException;
import vip.yazilim.spring.core.exception.general.InvalidUpdateException;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;
import vip.yazilim.spring.core.rest.ARestCrud;
import vip.yazilim.spring.core.rest.model.RestResponse;
import vip.yazilim.spring.core.service.ICrudService;
import vip.yazilim.spring.core.util.RestResponseFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static vip.yazilim.p2g.web.constant.Constants.API_P2G;

/**
 * @author mustafaarifsisman - 06.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@RestController
@RequestMapping(API_P2G + "/song")
public class SongRest extends ARestCrud<Song, Long> {

    @Autowired
    private ISongService songService;

    @Override
    protected ICrudService<Song, Long> getService() {
        return songService;
    }

    ///////////////////////////////
    // Super class CRUD controllers
    ///////////////////////////////

    @Override
    @HasRoomPrivilege(privilege = Privilege.SONG_ADD_AND_REMOVE)
    @UpdateRoomSongs
    @PostMapping({"/"})
    public RestResponse<Song> create(HttpServletRequest request, HttpServletResponse response, @RequestBody Song entity) {
        return super.create(request, response, entity);
    }

    @Override
    @HasRoomPrivilege(privilege = Privilege.SONG_GET)
    @GetMapping({"/{id}"})
    public RestResponse<Song> getById(HttpServletRequest request, HttpServletResponse response, @PathVariable Long id) {
        return super.getById(request, response, id);
    }

    @Override
    @HasRoomPrivilege(privilege = Privilege.SONG_GET)
    @GetMapping({"/"})
    public RestResponse<List<Song>> getAll(HttpServletRequest request, HttpServletResponse response) {
        return super.getAll(request, response);
    }

    @Override
    @HasRoomPrivilege(privilege = Privilege.SONG_UPDATE)
    @UpdateRoomSongs
    @PutMapping({"/"})
    public RestResponse<Song> update(HttpServletRequest request, HttpServletResponse response, @RequestBody Song entity) {
        return super.update(request, response, entity);
    }

    @Override
    @HasRoomPrivilege(privilege = Privilege.SONG_ADD_AND_REMOVE)
    @UpdateRoomSongs
    @DeleteMapping({"/{id}"})
    public RestResponse<Boolean> delete(HttpServletRequest request, HttpServletResponse response, @PathVariable Long id) {
        return super.delete(request, response, id);
    }

    ///////////////////////////////
    // Custom controllers
    ///////////////////////////////

    @HasRoomPrivilege(privilege = Privilege.SONG_GET)
    @GetMapping("/{roomId}/list")
    public RestResponse<List<Song>> getSongListByroomId(HttpServletRequest request, HttpServletResponse response, @PathVariable Long roomId) throws DatabaseException {
        return RestResponseFactory.generateResponse(songService.getSongListByRoomId(roomId), HttpStatus.OK, request, response);
    }

    @HasRoomPrivilege(privilege = Privilege.SONG_ADD_AND_REMOVE)
    @UpdateRoomSongs
    @PostMapping("/{roomId}")
    public RestResponse<List<Song>> addSongToRoom(HttpServletRequest request, HttpServletResponse response, @PathVariable Long roomId, @RequestBody SearchModel searchModel) throws InvalidArgumentException, SpotifyWebApiException, IOException, DatabaseException {
        return RestResponseFactory.generateResponse(songService.addSongToRoom(roomId, searchModel), HttpStatus.OK, request, response);
    }

    @HasRoomPrivilege(privilege = Privilege.SONG_ADD_AND_REMOVE)
    @UpdateRoomSongs
    @DeleteMapping("/{songId}")
    public RestResponse<Boolean> removeSongFromRoom(HttpServletRequest request, HttpServletResponse response, @PathVariable Long songId) throws DatabaseException, InvalidArgumentException {
        return RestResponseFactory.generateResponse(songService.removeSongFromRoom(songId), HttpStatus.OK, request, response);
    }

    @HasRoomPrivilege(privilege = Privilege.SONG_VOTE)
    @UpdateRoomSongs
    @PutMapping("/{songId}/upvote")
    public RestResponse<Integer> upvote(HttpServletRequest request, HttpServletResponse response, @PathVariable Long songId) throws InvalidUpdateException, DatabaseException, InvalidArgumentException {
        return RestResponseFactory.generateResponse(songService.upvote(songId), HttpStatus.OK, request, response);
    }

    @HasRoomPrivilege(privilege = Privilege.SONG_VOTE)
    @UpdateRoomSongs
    @PutMapping("/{songId}/downvote")
    public RestResponse<Integer> downvote(HttpServletRequest request, HttpServletResponse response, @PathVariable Long songId) throws InvalidUpdateException, DatabaseException, InvalidArgumentException {
        return RestResponseFactory.generateResponse(songService.downvote(songId), HttpStatus.OK, request, response);
    }

}
