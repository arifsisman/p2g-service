package vip.yazilim.p2g.web.service.spotify.impl;

import com.wrapper.spotify.model_objects.special.SearchResult;
import com.wrapper.spotify.model_objects.specification.*;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.p2g.web.enums.SearchType;
import vip.yazilim.p2g.web.model.SearchModel;
import vip.yazilim.p2g.web.service.p2g.IRoomService;
import vip.yazilim.p2g.web.service.p2g.ISongService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyRequestService;
import vip.yazilim.p2g.web.service.spotify.ISpotifySearchService;
import vip.yazilim.p2g.web.util.SecurityHelper;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class SpotifySearchService implements ISpotifySearchService {

    private final ISpotifyRequestService spotifyRequest;
    private final ISongService songService;
    private final IRoomService roomService;

    public SpotifySearchService(ISpotifyRequestService spotifyRequest, ISongService songService, IRoomService roomService) {
        this.spotifyRequest = spotifyRequest;
        this.songService = songService;
        this.roomService = roomService;
    }

    @Override
    public List<SearchModel> search(String query) {
        List<SearchModel> searchModels = new LinkedList<>();

        String type = SearchType.getAllTypes();
        SearchResult searchResult = spotifyRequest.execRequestAsync(spotifyApi -> spotifyApi.searchItem(query, type).limit(15).build(), SecurityHelper.getUserAccessToken());

        searchModels.addAll(Arrays.stream(searchResult.getTracks().getItems()).map(SearchModel::new).collect(Collectors.toCollection(LinkedList::new)));
        searchModels.addAll(Arrays.stream(searchResult.getAlbums().getItems()).map(SearchModel::new).collect(Collectors.toCollection(LinkedList::new)));
        searchModels.addAll(Arrays.stream(searchResult.getPlaylists().getItems()).map(SearchModel::new).collect(Collectors.toCollection(LinkedList::new)));

        return searchModels;
    }

    /**
     * @return if room queue is empty getRecommendations based users top tracks
     * else get recommendations based on last song (playing, paused, next, previous)
     */
    @Override
    public List<SearchModel> getRecommendations() {
        List<SearchModel> recommendationsList = new LinkedList<>();
        String accessToken = SecurityHelper.getUserAccessToken();

        Optional<Room> roomOpt = roomService.getRoomByUserId(SecurityHelper.getUserId());
        if (roomOpt.isPresent()) {
            Long roomId = roomOpt.get().getId();
            Optional<Song> playingOrPausedOrPlayed = songService.getRecentSong(roomId, true);

            if (playingOrPausedOrPlayed.isPresent()) {
                Recommendations recommendations = spotifyRequest.execRequestAsync(spotifyApi -> spotifyApi.getRecommendations().seed_tracks(playingOrPausedOrPlayed.get().getSongId()).limit(20).build(), accessToken);
                TrackSimplified[] recommendationsTrackList = recommendations.getTracks();
                List<String> trackSimplifiedIds = new LinkedList<>();

                for (TrackSimplified trackSimplified : recommendationsTrackList) {
                    trackSimplifiedIds.add(trackSimplified.getId());
                }

                return getByTracksIds(trackSimplifiedIds);
            } else {
                Paging<Track> trackPaging = spotifyRequest.execRequestAsync(spotifyApi -> spotifyApi.getUsersTopTracks().limit(20).build(), accessToken);
                Track[] tracks = trackPaging.getItems();

                for (Track track : tracks) {
                    recommendationsList.add(new SearchModel(track));
                }

                return recommendationsList;
            }
        } else {
            throw new NoSuchElementException("User not in any room.");
        }
    }

    @Override
    public SearchModel getByTrackId(String id) {
        return new SearchModel(spotifyRequest.execRequestAsync(spotifyApi -> spotifyApi.getTrack(id).build(), SecurityHelper.getUserAccessToken()));
    }

    @Override
    public List<SearchModel> getByAlbumId(String albumId) {
        List<SearchModel> searchModelList = new LinkedList<>();

        String accessToken = SecurityHelper.getUserAccessToken();
        Album album = spotifyRequest.execRequestAsync((spotifyApi) -> spotifyApi.getAlbum(albumId).build(), accessToken);

        Paging<TrackSimplified> trackSimplifiedPaging = spotifyRequest.execRequestAsync((spotifyApi) -> spotifyApi.getAlbumsTracks(albumId).build(), accessToken);
        TrackSimplified[] tracks = trackSimplifiedPaging.getItems();

        String imageUrl = null;
        Image[] albumImages = album.getImages();
        if (albumImages.length > 0) {
            imageUrl = albumImages[0].getUrl();
        }

        for (TrackSimplified t : tracks) {
            SearchModel searchModel = new SearchModel(t);
            searchModel.setImageUrl(imageUrl);
            searchModel.setAlbumName(album.getName());
            searchModelList.add(searchModel);
        }

        return searchModelList;
    }

    @Override
    public List<SearchModel> getByPlaylistId(String playlistId) {
        List<SearchModel> searchModelList = new LinkedList<>();

        Playlist playlist = spotifyRequest.execRequestAsync(spotifyApi -> spotifyApi.getPlaylist(playlistId).build(), SecurityHelper.getUserAccessToken());
        PlaylistTrack[] playlistTracks = playlist.getTracks().getItems();

        for (PlaylistTrack p : playlistTracks) {
            SearchModel searchModel = new SearchModel(p);
            //TODO: searchModel albumName changed to playlistName
            searchModel.setAlbumName(playlist.getName());
            searchModelList.add(searchModel);
        }

        return searchModelList;
    }

    private List<SearchModel> getByTracksIds(List<String> ids) {
        String[] idsArray = ids.toArray(new String[0]);
        Track[] tracks = spotifyRequest.execRequestAsync(spotifyApi -> spotifyApi.getSeveralTracks(idsArray).build(), SecurityHelper.getUserAccessToken());
        return Arrays.stream(tracks).map(SearchModel::new).collect(Collectors.toList());
    }

}
