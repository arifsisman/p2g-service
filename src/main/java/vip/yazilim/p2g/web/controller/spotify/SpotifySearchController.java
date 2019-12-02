package vip.yazilim.p2g.web.controller.spotify;

import com.wrapper.spotify.enums.ModelObjectType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.entity.model.SearchModel;
import vip.yazilim.p2g.web.exception.RequestException;
import vip.yazilim.p2g.web.service.p2g.IRoomService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyAlbumService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyPlaylistService;
import vip.yazilim.p2g.web.service.spotify.ISpotifySearchService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyTrackService;
import vip.yazilim.spring.utils.exception.runtime.ServiceException;

import java.util.List;

/**
 * @author mustafaarifsisman - 1.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@RestController
public class SpotifySearchController {

    @Autowired
    private ISpotifyTrackService spotifyTrackService;

    @Autowired
    private ISpotifyAlbumService spotifyAlbumService;

    @Autowired
    private ISpotifyPlaylistService spotifyPlaylistService;

    @Autowired
    private ISpotifySearchService spotifySearchService;

    @Autowired
    private IRoomService roomService;

    @GetMapping("/rooms")
    public List<Room> rooms() {
        try {
            return roomService.getAll();
        } catch (Exception e) {
            throw new ServiceException("An error occurred while getting rooms.", e);
        }
    }

    @GetMapping("/spotify/search/{query}")
    public List<SearchModel> search(@PathVariable String query) {
        try {
            return spotifySearchService.search(query, ModelObjectType.TRACK, ModelObjectType.ALBUM, ModelObjectType.PLAYLIST);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @GetMapping("/spotify/song/{id}")
    public SearchModel getSong(@PathVariable String id) {
        try {
            return spotifyTrackService.getTrack(id);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    @GetMapping("/spotify/songs/{ids}")
    public List<SearchModel> getSongList(@PathVariable String[] ids) {
        List<SearchModel> searchModelList;
        try {
            searchModelList = spotifyTrackService.getSeveralTracks(ids);
        } catch (RequestException e) {
            throw new ServiceException(e);
        }
        return searchModelList;
    }

    @GetMapping("/spotify/album/{albumId}/songs")
    public List<SearchModel> getAlbumSongList(@PathVariable String albumId) {
        try {
            return spotifyAlbumService.getSongs(albumId);
        } catch (RequestException e) {
            throw new ServiceException(e);
        }
    }

    @GetMapping("/spotify/playlist/{playlistId}/songs")
    public List<SearchModel> getPlaylistSongList(@PathVariable String playlistId) {
        try {
            return spotifyPlaylistService.getSongs(playlistId);
        } catch (RequestException e) {
            throw new ServiceException(e);
        }
    }
}
