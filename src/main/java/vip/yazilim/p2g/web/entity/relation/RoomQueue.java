package vip.yazilim.p2g.web.entity.relation;

import lombok.Data;
import vip.yazilim.p2g.web.constant.Constants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = Constants.RELATION_TABLE_PREFIX + "room_queue")
@Data
public class RoomQueue implements Serializable {

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
    private Long durationMs;

    // Queue
    @Column(name = "queue_status", nullable = false)
    private String queueStatus;

    @Column(name = "queued_time")
    private Date queuedTime;

    @Column(name = "playing_time")
    private Date playingTime;

    @Column(name = "current_ms")
    private Long currentMs;

    private Integer votes;

}
