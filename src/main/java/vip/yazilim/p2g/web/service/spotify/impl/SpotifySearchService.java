package vip.yazilim.p2g.web.service.spotify.impl;

import com.wrapper.spotify.model_objects.special.SearchResult;
import com.wrapper.spotify.model_objects.specification.AlbumSimplified;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Recommendations;
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
import vip.yazilim.p2g.web.util.SpotifyHelper;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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
            SearchResult songSearchResult = spotifyRequest.execRequestSync(spotifyApi -> spotifyApi.searchItem(q, SONG.getType()).limit(15).build(), SecurityHelper.getUserAccessToken());
            searchModelList.addAll(SpotifyHelper.convertAbstractModelObjectToSearchModelList(songSearchResult.getTracks().getItems()));
            return searchModelList;
        }

        for (SearchType s : searchTypes) {
            if (s == SearchType.SONG) {
                SearchResult songSearchResult = spotifyRequest.execRequestSync(spotifyApi -> spotifyApi.searchItem(q, SearchType.SONG.getType()).limit(15).build(), SecurityHelper.getUserAccessToken());
                searchModelList.addAll(SpotifyHelper.convertAbstractModelObjectToSearchModelList(songSearchResult.getTracks().getItems()));
            } else if (s == SearchType.ALBUM) {
                SearchResult songSearchResult = spotifyRequest.execRequestSync(spotifyApi -> spotifyApi.searchItem(q, SearchType.ALBUM.getType()).limit(5).build(), SecurityHelper.getUserAccessToken());
                searchModelList.addAll(SpotifyHelper.convertAbstractModelObjectToSearchModelList(songSearchResult.getAlbums().getItems()));
            } else if (s == SearchType.PLAYLIST) {
                SearchResult songSearchResult = spotifyRequest.execRequestSync(spotifyApi -> spotifyApi.searchItem(q, SearchType.PLAYLIST.getType()).limit(5).build(), SecurityHelper.getUserAccessToken());
                searchModelList.addAll(SpotifyHelper.convertAbstractModelObjectToSearchModelList(songSearchResult.getPlaylists().getItems()));
            }
        }

        return searchModelList;
    }

    /**
     * @return if room queue is empty getRecommendations based on new releases else get recommendations based on playing song
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
                Recommendations recommendations = spotifyRequest.execRequestSync(spotifyApi -> spotifyApi.getRecommendations().seed_tracks(playingOrPausedOrPlayed.get().getSongId()).limit(15).build(), accessToken);
                TrackSimplified[] recommendationsTrackList = recommendations.getTracks();
                List<String> trackSimplifiedIds = new LinkedList<>();

                for (TrackSimplified trackSimplified : recommendationsTrackList) {
                    trackSimplifiedIds.add(trackSimplified.getId());
                }

                return spotifyTrackService.getSeveralTracks(trackSimplifiedIds.toArray(new String[0]));
            } else {
                Paging<AlbumSimplified> newReleases = spotifyRequest.execRequestSync(spotifyApi -> spotifyApi.getListOfNewReleases().limit(15).build(), accessToken);
                AlbumSimplified[] albumSimplifiedList = newReleases.getItems();

                for (AlbumSimplified albumSimplified : albumSimplifiedList) {
                    recommendationsList.add(new SearchModel(albumSimplified));
                }

                return recommendationsList;
            }
        } else {
            throw new NoSuchElementException("User not in any room.");
        }
    }


}
