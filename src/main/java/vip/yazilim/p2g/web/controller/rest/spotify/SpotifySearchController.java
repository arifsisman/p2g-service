package vip.yazilim.p2g.web.controller.rest.spotify;

import com.wrapper.spotify.enums.ModelObjectType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vip.yazilim.p2g.web.exception.RequestException;
import vip.yazilim.p2g.web.model.SearchModel;
import vip.yazilim.p2g.web.service.spotify.ISpotifyAlbumService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyPlaylistService;
import vip.yazilim.p2g.web.service.spotify.ISpotifySearchService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyTrackService;
import vip.yazilim.spring.core.exception.web.ServiceException;

import java.util.List;

import static vip.yazilim.p2g.web.constant.Constants.API_SPOTIFY;

/**
 * @author mustafaarifsisman - 1.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@RestController
@RequestMapping(API_SPOTIFY + "/search")
public class SpotifySearchController {

    @Autowired
    private ISpotifyTrackService spotifyTrackService;

    @Autowired
    private ISpotifyAlbumService spotifyAlbumService;

    @Autowired
    private ISpotifyPlaylistService spotifyPlaylistService;

    @Autowired
    private ISpotifySearchService spotifySearchService;

    @GetMapping("/{query}")
    public List<SearchModel> search(@PathVariable String query) {
        try {
            return spotifySearchService.search(query, ModelObjectType.TRACK, ModelObjectType.ALBUM, ModelObjectType.PLAYLIST);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @GetMapping("/song/{id}")
    public SearchModel getSong(@PathVariable String id) {
        try {
            return spotifyTrackService.getTrack(id);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @GetMapping("/songs/{ids}")
    public List<SearchModel> getSongList(@PathVariable String[] ids) {
        List<SearchModel> searchModelList;
        try {
            searchModelList = spotifyTrackService.getSeveralTracks(ids);
        } catch (RequestException e) {
            throw new ServiceException(e);
        }
        return searchModelList;
    }

    @GetMapping("/album/{albumId}/songs")
    public List<SearchModel> getAlbumSongList(@PathVariable String albumId) {
        try {
            return spotifyAlbumService.getSongs(albumId);
        } catch (RequestException e) {
            throw new ServiceException(e);
        }
    }

    @GetMapping("/playlist/{playlistId}/songs")
    public List<SearchModel> getPlaylistSongList(@PathVariable String playlistId) {
        try {
            return spotifyPlaylistService.getSongs(playlistId);
        } catch (RequestException e) {
            throw new ServiceException(e);
        }
    }
}
