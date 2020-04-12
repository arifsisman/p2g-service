package vip.yazilim.p2g.web.service.spotify.impl;

import com.wrapper.spotify.model_objects.special.SearchResult;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Recommendations;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.model_objects.specification.TrackSimplified;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.Room;
import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.p2g.web.enums.SearchType;
import vip.yazilim.p2g.web.model.SearchModel;
import vip.yazilim.p2g.web.service.p2g.IRoomService;
import vip.yazilim.p2g.web.service.p2g.ISongService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyRequestService;
import vip.yazilim.p2g.web.service.spotify.ISpotifySearchService;
import vip.yazilim.p2g.web.service.spotify.ISpotifyTrackService;
import vip.yazilim.p2g.web.util.SecurityHelper;

import java.util.*;
import java.util.stream.Collectors;

import static vip.yazilim.p2g.web.enums.SearchType.SONG;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class SpotifySearchService implements ISpotifySearchService {

    @Autowired
    private ISpotifyRequestService spotifyRequest;

    @Autowired
    private ISongService songService;

    @Autowired
    private IRoomService roomService;

    @Autowired
    private ISpotifyTrackService spotifyTrackService;

    @Override
    public List<SearchModel> search(String q, SearchType... searchTypes) {
        List<SearchModel> searchModelList = new LinkedList<>();

        if (searchTypes.length == 0) {
            SearchResult songSearchResult = spotifyRequest.execRequestAsync(spotifyApi -> spotifyApi.searchItem(q, SONG.getType()).limit(15).build(), SecurityHelper.getUserAccessToken());
            searchModelList.addAll(Arrays.stream(songSearchResult.getTracks().getItems()).map(SearchModel::new).collect(Collectors.toList()));
            return searchModelList;
        }

        for (SearchType s : searchTypes) {
            if (s == SearchType.SONG) {
                SearchResult songSearchResult = spotifyRequest.execRequestAsync(spotifyApi -> spotifyApi.searchItem(q, SearchType.SONG.getType()).limit(15).build(), SecurityHelper.getUserAccessToken());
                searchModelList.addAll(Arrays.stream(songSearchResult.getTracks().getItems()).map(SearchModel::new).collect(Collectors.toList()));
            } else if (s == SearchType.ALBUM) {
                SearchResult songSearchResult = spotifyRequest.execRequestAsync(spotifyApi -> spotifyApi.searchItem(q, SearchType.ALBUM.getType()).limit(5).build(), SecurityHelper.getUserAccessToken());
                searchModelList.addAll(Arrays.stream(songSearchResult.getAlbums().getItems()).map(SearchModel::new).collect(Collectors.toList()));
            } else if (s == SearchType.PLAYLIST) {
                SearchResult songSearchResult = spotifyRequest.execRequestAsync(spotifyApi -> spotifyApi.searchItem(q, SearchType.PLAYLIST.getType()).limit(5).build(), SecurityHelper.getUserAccessToken());
                searchModelList.addAll(Arrays.stream(songSearchResult.getPlaylists().getItems()).map(SearchModel::new).collect(Collectors.toList()));
            }
        }

        return searchModelList;
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
            Optional<Song> playingOrPausedOrPlayed = songService.getPlayingOrPausedOrNextOrPlayedSong(roomId);

            if (playingOrPausedOrPlayed.isPresent()) {
                Recommendations recommendations = spotifyRequest.execRequestAsync(spotifyApi -> spotifyApi.getRecommendations().seed_tracks(playingOrPausedOrPlayed.get().getSongId()).limit(15).build(), accessToken);
                TrackSimplified[] recommendationsTrackList = recommendations.getTracks();
                List<String> trackSimplifiedIds = new LinkedList<>();

                for (TrackSimplified trackSimplified : recommendationsTrackList) {
                    trackSimplifiedIds.add(trackSimplified.getId());
                }

                return spotifyTrackService.getSeveralTracks(trackSimplifiedIds.toArray(new String[0]));
            } else {
                Paging<Track> trackPaging = spotifyRequest.execRequestAsync(spotifyApi -> spotifyApi.getUsersTopTracks().limit(15).build(), accessToken);
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


}
