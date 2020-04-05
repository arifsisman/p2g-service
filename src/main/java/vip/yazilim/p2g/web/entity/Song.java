package vip.yazilim.p2g.web.entity;

import lombok.Data;
import vip.yazilim.p2g.web.constant.Constants;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = Constants.TABLE_PREFIX + "song")
@Data
public class Song implements Serializable {

    @Id
    @SequenceGenerator(name = "song_id_seq", sequenceName = "song_id_seq", allocationSize = 7)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "song_id_seq")
    @Column(name = "id", unique = true, updatable = false, nullable = false, columnDefinition = "serial")
    private Long id;

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @Column(name = "song_id")
    private String songId;

    @Column(name = "song_name", nullable = false)
    private String songName;

    @Column(name = "album_name")
    private String albumName;

    @Column(name = "artist_names")
    @ElementCollection(targetClass = String.class)
    private List<String> artistNames;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "duration_ms", nullable = false)
    private Integer durationMs;

    @Column(name = "song_status", nullable = false)
    private String songStatus;

    @Column(name = "queued_time", columnDefinition = "TIMESTAMP")
    private LocalDateTime queuedTime;

    @Column(name = "playing_time", columnDefinition = "TIMESTAMP")
    private LocalDateTime playingTime;

    @Column(name = "current_ms")
    private Integer currentMs;

    @Column(name = "repeat_flag")
    private Boolean repeatFlag;

    private Integer votes;

    @Override
    public String toString() {
        return (getSongName() + " - " + getArtistNames()).replace("[", "").replace("]", "");
    }
}
