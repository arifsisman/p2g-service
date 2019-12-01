package vip.yazilim.p2g.web.entity.relation;

import lombok.Data;
import vip.yazilim.p2g.web.constant.Constants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = Constants.RELATION_TABLE_PREFIX + "room_queue")
@Data
public class RoomQueue implements Serializable {

    @Id
    private String uuid;

    @Column(name = "room_uuid", nullable = false)
    private String roomUuid;

    @Column(name = "song_uri")
    private String songUri;

    @Column(name = "song_id", nullable = false)
    private String songId;

    @Column(name = "current_ms")
    private Integer currentMs;

    @Column(name = "duration_ms", nullable = false)
    private Integer durationMs;

    @Column(name = "queued_time")
    private String queuedTime;

    private int votes;
    private String status;

}
