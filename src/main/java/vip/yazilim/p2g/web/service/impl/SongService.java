package vip.yazilim.p2g.web.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.relation.RoomQueue;
import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.p2g.web.entity.relation.AlbumSong;
import vip.yazilim.p2g.web.entity.relation.PlaylistSong;
import vip.yazilim.p2g.web.repository.ISongRepo;
import vip.yazilim.p2g.web.service.IQueueService;
import vip.yazilim.p2g.web.service.ISongService;
import vip.yazilim.p2g.web.service.relation.IAlbumSongService;
import vip.yazilim.p2g.web.service.relation.IPlaylistSongService;
import vip.yazilim.p2g.web.util.DBHelper;
import vip.yazilim.spring.utils.exception.DatabaseException;
import vip.yazilim.spring.utils.service.ACrudServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class SongService extends ACrudServiceImpl<Song, String> implements ISongService {

    // static fields
    private Logger LOGGER = LoggerFactory.getLogger(SongService.class);

    private static final String UNDEFINED_SONG_NAME = "Undefined Song";

    @Autowired
    private ISongRepo songRepo;

    @Autowired
    private IQueueService queueService;

    @Autowired
    private IAlbumSongService albumSongService;

    @Autowired
    private IPlaylistSongService playlistSongService;

    @Override
    protected JpaRepository<Song, String> getRepository() {
        return songRepo;
    }

    @Override
    protected String getId(Song entity) {
        return entity.getUuid();
    }

    @Override
    protected Song preInsert(Song entity) {
        entity.setUuid(DBHelper.getRandomUuid());
        return entity;
    }


    @Override
    public Optional<Song> getSongByName(String songName) throws DatabaseException {
        Optional<Song> songOptional;

        try {
            songOptional = songRepo.findByName(songName);
        } catch (Exception exception) {
            String errorMessage = String.format("An error occurred while getting Song with name[%s]", songName);
            throw new DatabaseException(errorMessage, exception);
        }

        return songOptional;
    }

    @Override
    public List<Song> getSongListByRoomUuid(String roomUuid) throws DatabaseException {
        List<RoomQueue> roomQueueSongList = queueService.getQueueListByRoomUuid(roomUuid);
        List<String> songUuidList = roomQueueSongList.stream().map((RoomQueue::getSongUuid)).collect(Collectors.toList());
        return getSongListBySongUuidList(songUuidList);
    }

    @Override
    public Song getRoomCurrentSongUuid(String roomUuid) throws DatabaseException {
        List<RoomQueue> roomQueueSongs = queueService.getQueueListByRoomUuid(roomUuid);

        if (!roomQueueSongs.isEmpty()) {
            String songUuid;

            //TODO: implement sort by queue votes
            //roomQueueSongList.sort();
            songUuid = roomQueueSongs.get(0).getSongUuid();

            Optional<Song> song = getById(songUuid);

            //TODO: check return song uuid or id
            if (song.isPresent())
                return song.get();
        }

        return null;
    }

    @Override
    public List<Song> getSongListByAlbumUuid(String albumUuid) throws DatabaseException {
        List<AlbumSong> albumSongList = albumSongService.getAlbumSongListByAlbum(albumUuid);
        List<String> songUuidList = albumSongList.stream().map((AlbumSong::getSongUuid)).collect(Collectors.toList());
        return getSongListBySongUuidList(songUuidList);
    }

    @Override
    public List<Song> getSongListByPlaylistUuid(String playlistUuid) throws DatabaseException {
        List<PlaylistSong> playlistSongList = playlistSongService.getPlaylistSongListByPlaylist(playlistUuid);
        List<String> songUuidList = playlistSongList.stream().map((PlaylistSong::getSongUuid)).collect(Collectors.toList());
        return getSongListBySongUuidList(songUuidList);
    }

    private List<Song> getSongListBySongUuidList(List<String> songUuidList) throws DatabaseException {
        List<Song> songList = new ArrayList<>();

        for (String songUuid : songUuidList) {
            Song song = getSafeSong(songUuid);
            songList.add(song);
        }

        return songList;
    }

    @Override
    public Song getSafeSong(String uuid) throws DatabaseException {
        Optional<Song> song = getById(uuid);

        if (song.isPresent()) {
            return song.get();
        }

        LOGGER.warn("Song {} undefined", uuid);
        return getUndefinedSong();
    }

    private Song getUndefinedSong() {
        Song undefinedSong = new Song();
        undefinedSong.setUuid(DBHelper.getRandomUuid());
        undefinedSong.setName(UNDEFINED_SONG_NAME);
        return undefinedSong;
    }

    @Override
    public Song createSafeSong(Song song) {
        Optional<Song> existingSong = Optional.empty();

        try {
            existingSong = getById(song.getUuid());
        } catch (DatabaseException e) {
            LOGGER.error("An error occurred while getting song.");
        }

        if (existingSong.isPresent()) {
            LOGGER.trace("Song already exists.");
            return existingSong.get();
        } else {
            try {
                create(song);
            } catch (DatabaseException e) {
                LOGGER.error("An error occurred while creating song.");
            }
        }

        return song;
    }
}
