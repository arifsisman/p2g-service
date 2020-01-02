package vip.yazilim.p2g.web.entity;

import lombok.Data;
import vip.yazilim.p2g.web.constant.Constants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = Constants.TABLE_PREFIX + "song")
@Data
public class Song implements Serializable {

    @Id
    private String uuid;

    @Column(name = "room_uuid", nullable = false)
    private String roomUuid;

    // Song
    @Column(name = "song_id", nullable = false)
    private String songId;

    @Column(name = "song_uri")
    private String songUri;

    @Column(name = "song_name", nullable = false)
    private String songName;

    @Column(name = "album_name")
    private String albumName;

    @Column(name = "artists")
    private String[] artists;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "duration_ms", nullable = false)
    private Integer durationMs;

    // Queue
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

}
