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
    private String name;
    private String artist;

    @Column(name = "room_uuid")
    private String roomUuid;
}
