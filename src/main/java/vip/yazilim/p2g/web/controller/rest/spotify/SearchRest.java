package vip.yazilim.p2g.web.controller.rest.spotify;

import com.wrapper.spotify.enums.ModelObjectType;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vip.yazilim.p2g.web.config.security.annotation.HasRoomPrivilege;
import vip.yazilim.p2g.web.constant.Privilege;
import vip.yazilim.p2g.web.model.SearchModel;
import vip.yazilim.p2g.web.service.spotify.ISpotifyAlbumService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyPlaylistService;
import vip.yazilim.p2g.web.service.spotify.ISpotifySearchService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyTrackService;
import vip.yazilim.spring.core.exception.web.ServiceException;
import vip.yazilim.spring.core.rest.model.RestResponse;
import vip.yazilim.spring.core.util.RestResponseFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static vip.yazilim.p2g.web.constant.Constants.API_SPOTIFY;

/**
 * @author mustafaarifsisman - 1.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@RestController
@RequestMapping(API_SPOTIFY + "/search")
public class SearchRest {

    @Autowired
    private ISpotifyTrackService spotifyTrackService;

    @Autowired
    private ISpotifyAlbumService spotifyAlbumService;

    @Autowired
    private ISpotifyPlaylistService spotifyPlaylistService;

    @Autowired
    private ISpotifySearchService spotifySearchService;

    @HasRoomPrivilege(privilege = Privilege.SONG_SEARCH)
    @GetMapping("/{query}")
    public RestResponse<List<SearchModel>> search(HttpServletRequest request, HttpServletResponse response, @PathVariable String query) {
        List<SearchModel> searchModelList;

        try {
            searchModelList = spotifySearchService.search(query, ModelObjectType.TRACK, ModelObjectType.ALBUM, ModelObjectType.PLAYLIST);
        } catch (Exception e) {
            throw new ServiceException(e);
        }

        return RestResponseFactory.generateResponse(searchModelList, HttpStatus.OK, request, response);
    }

    @HasRoomPrivilege(privilege = Privilege.SONG_SEARCH)
    @GetMapping("/song/{id}")
    public RestResponse<SearchModel> getSong(HttpServletRequest request, HttpServletResponse response, @PathVariable String id) {
        SearchModel searchModel;

        try {
            searchModel = spotifyTrackService.getTrack(id);
        } catch (Exception e) {
            throw new ServiceException(e);
        }

        return RestResponseFactory.generateResponse(searchModel, HttpStatus.OK, request, response);
    }

    @HasRoomPrivilege(privilege = Privilege.SONG_SEARCH)
    @GetMapping("/songs/{ids}")
    public RestResponse<List<SearchModel>> getSongList(HttpServletRequest request, HttpServletResponse response, @PathVariable String ids) {
        List<SearchModel> searchModelList;

        String[] idArray = ids.split(",");

        try {
            searchModelList = spotifyTrackService.getSeveralTracks(idArray);
        } catch (Exception e) {
            throw new ServiceException(e);
        }

        return RestResponseFactory.generateResponse(searchModelList, HttpStatus.OK, request, response);
    }

    @HasRoomPrivilege(privilege = Privilege.SONG_SEARCH)
    @GetMapping("/album/{albumId}/songs")
    public RestResponse<List<SearchModel>> getAlbumSongList(HttpServletRequest request, HttpServletResponse response, @PathVariable String albumId) throws IOException, SpotifyWebApiException {
        return RestResponseFactory.generateResponse(spotifyAlbumService.getSongs(albumId), HttpStatus.OK, request, response);
    }

    @HasRoomPrivilege(privilege = Privilege.SONG_SEARCH)
    @GetMapping("/playlist/{playlistId}/songs")
    public RestResponse<List<SearchModel>> getPlaylistSongList(HttpServletRequest request, HttpServletResponse response, @PathVariable String playlistId) {
        List<SearchModel> searchModelList;

        try {
            searchModelList = spotifyPlaylistService.getSongs(playlistId);
        } catch (Exception e) {
            throw new ServiceException(e);
        }

        return RestResponseFactory.generateResponse(searchModelList, HttpStatus.OK, request, response);
    }
}
