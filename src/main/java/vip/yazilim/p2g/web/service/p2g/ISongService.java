package vip.yazilim.p2g.web.service.p2g;

import vip.yazilim.libs.springcore.service.ICrudService;
import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.p2g.web.enums.SongStatus;
import vip.yazilim.p2g.web.model.SearchModel;

import java.util.List;
import java.util.Optional;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ISongService extends ICrudService<Song, Long> {
    /**
     * @param roomId roomId
     * @return songList order by playing status, votes and queued time. Not includes played songs.
     */
    List<Song> getSongListByRoomId(Long roomId);

    Optional<Song> getSongByRoomIdAndStatus(Long roomId, SongStatus songStatus);

    Optional<Song> getPausedSong(Long roomId);

    Optional<Song> getPlayingSong(Long roomId);

    Optional<Song> getPlayingOrPausedSong(Long roomId);

    Optional<Song> getNextSong(Long roomId);

    Optional<Song> getPreviousSong(Long roomId);

    boolean addSongToRoom(Long roomId, List<SearchModel> searchModelList);

    boolean removeSongFromRoom(Long songId);

    boolean deleteRoomSongList(Long roomId);

    int upvote(Long songId);

    int downvote(Long songId);

    List<Song> getActiveSongs();

    void updateSongStatus(Song song, SongStatus songStatus);
}
