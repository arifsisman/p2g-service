package vip.yazilim.p2g.web.controller.rest.spotify;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vip.yazilim.libs.springcore.rest.model.RestResponse;
import vip.yazilim.p2g.web.config.annotation.HasRoomPrivilege;
import vip.yazilim.p2g.web.constant.enums.Privilege;
import vip.yazilim.p2g.web.constant.enums.SearchType;
import vip.yazilim.p2g.web.model.SearchModel;
import vip.yazilim.p2g.web.service.spotify.ISpotifyAlbumService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyPlaylistService;
import vip.yazilim.p2g.web.service.spotify.ISpotifySearchService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyTrackService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
        return RestResponse
                .generateResponse(spotifySearchService
                                .search(query, SearchType.SONG, SearchType.ALBUM, SearchType.PLAYLIST),
                        HttpStatus.OK, request, response);
    }

    @HasRoomPrivilege(privilege = Privilege.SONG_SEARCH)
    @GetMapping("/song/{id}")
    public RestResponse<SearchModel> getSong(HttpServletRequest request, HttpServletResponse response, @PathVariable String id) {
        return RestResponse.generateResponse(spotifyTrackService.getTrack(id), HttpStatus.OK, request, response);
    }

    @HasRoomPrivilege(privilege = Privilege.SONG_SEARCH)
    @GetMapping("/songs/{ids}")
    public RestResponse<List<SearchModel>> getSongList(HttpServletRequest request, HttpServletResponse response, @PathVariable String ids) {
        String[] idArray = ids.split(",");
        return RestResponse.generateResponse(spotifyTrackService.getSeveralTracks(idArray), HttpStatus.OK, request, response);
    }

    @HasRoomPrivilege(privilege = Privilege.SONG_SEARCH)
    @GetMapping("/album/{albumId}/songs")
    public RestResponse<List<SearchModel>> getAlbumSongList(HttpServletRequest request, HttpServletResponse response, @PathVariable String albumId) {
        return RestResponse.generateResponse(spotifyAlbumService.getSongs(albumId), HttpStatus.OK, request, response);
    }

    @HasRoomPrivilege(privilege = Privilege.SONG_SEARCH)
    @GetMapping("/playlist/{playlistId}/songs")
    public RestResponse<List<SearchModel>> getPlaylistSongList(HttpServletRequest request, HttpServletResponse response, @PathVariable String playlistId) {
        return RestResponse.generateResponse(spotifyPlaylistService.getSongs(playlistId), HttpStatus.OK, request, response);
    }
}
