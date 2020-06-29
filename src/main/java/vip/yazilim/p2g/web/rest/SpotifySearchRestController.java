package vip.yazilim.p2g.web.rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vip.yazilim.libs.springcore.rest.model.RestResponse;
import vip.yazilim.p2g.web.config.annotation.HasRoomPrivilege;
import vip.yazilim.p2g.web.enums.Privilege;
import vip.yazilim.p2g.web.model.SearchModel;
import vip.yazilim.p2g.web.service.spotify.ISpotifySearchService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static vip.yazilim.p2g.web.constant.Constants.API_SPOTIFY;

/**
 * @author mustafaarifsisman - 29.06.2020
 * @contact mustafaarifsisman@gmail.com
 */
@RestController
@RequestMapping(API_SPOTIFY)
public class SpotifySearchRestController {

    private final ISpotifySearchService spotifySearchService;

    public SpotifySearchRestController(ISpotifySearchService spotifySearchService) {
        this.spotifySearchService = spotifySearchService;
    }

    @HasRoomPrivilege(privilege = Privilege.SONG_SEARCH)
    @GetMapping("/search/{query}")
    public RestResponse<List<SearchModel>> search(HttpServletRequest request, HttpServletResponse response, @PathVariable String query) {
        return RestResponse.generateResponse(spotifySearchService.search(query), HttpStatus.OK, request, response);
    }

    @GetMapping("/search/recommendations")
    public RestResponse<List<SearchModel>> getRecommendations(HttpServletRequest request, HttpServletResponse response) {
        return RestResponse.generateResponse(spotifySearchService.getRecommendations(), HttpStatus.OK, request, response);
    }

    @HasRoomPrivilege(privilege = Privilege.SONG_SEARCH)
    @GetMapping("/search/song/{id}")
    public RestResponse<SearchModel> getSong(HttpServletRequest request, HttpServletResponse response, @PathVariable String id) {
        return RestResponse.generateResponse(spotifySearchService.getByTrackId(id), HttpStatus.OK, request, response);
    }

    @HasRoomPrivilege(privilege = Privilege.SONG_SEARCH)
    @GetMapping("/search/album/{albumId}/songs")
    public RestResponse<List<SearchModel>> getAlbumSongList(HttpServletRequest request, HttpServletResponse response, @PathVariable String albumId) {
        return RestResponse.generateResponse(spotifySearchService.getByAlbumId(albumId), HttpStatus.OK, request, response);
    }

    @HasRoomPrivilege(privilege = Privilege.SONG_SEARCH)
    @GetMapping("/search/playlist/{playlistId}/songs")
    public RestResponse<List<SearchModel>> getPlaylistSongList(HttpServletRequest request, HttpServletResponse response, @PathVariable String playlistId) {
        return RestResponse.generateResponse(spotifySearchService.getByPlaylistId(playlistId), HttpStatus.OK, request, response);
    }
}
