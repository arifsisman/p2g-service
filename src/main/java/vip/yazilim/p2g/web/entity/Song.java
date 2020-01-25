package vip.yazilim.p2g.web.entity;

import lombok.Data;
import org.joda.time.LocalDateTime;
import vip.yazilim.p2g.web.constant.Constants;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = Constants.TABLE_PREFIX + "song")
@Data
public class Song implements Serializable {

    @Id
    @SequenceGenerator(name = "song_id_seq", sequenceName = "song_id_seq", allocationSize = 7)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "song_id_seq")
    @Column(name = "id", unique = true, updatable = false, nullable = false, columnDefinition = "serial")
    private Long id;

    @Column(name = "room_id", nullable = false, length = 64)
    private Long roomId;

    // Song
    @Column(name = "song_id", nullable = false, length = 64)
    private String songId;

    @Column(name = "song_uri", length = 64)
    private String songUri;

    @Column(name = "song_name", nullable = false, length = 64)
    private String songName;

    @Column(name = "album_name", length = 64)
    private String albumName;

    @Column(name = "artists")
    private String[] artists;

    @Column(name = "image_url", length = 128)
    private String imageUrl;

    @Column(name = "duration_ms", nullable = false)
    private Integer durationMs;

    // Queue
    @Column(name = "song_status", nullable = false)
    private String songStatus;

    @Column(name = "queued_time")
    private LocalDateTime queuedTime;

    @Column(name = "playing_time")
    private LocalDateTime playingTime;

    @Column(name = "current_ms")
    private Integer currentMs;

    @Column(name = "repeat_flag")
    private Boolean repeatFlag;

    private Integer votes;

}
