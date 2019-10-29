package vip.yazilim.p2g.web.entity;

import lombok.Data;
import vip.yazilim.p2g.web.constant.Constants;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = Constants.TABLE_PREFIX + "song")
@Data
public class Song implements Serializable {

    @Id
    private String uuid;

    @Column(name = "song_id")
    private String songId;

    private String uri;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String artists;

    @Column(name = "duration_ms")
    private String durationMs;

}
