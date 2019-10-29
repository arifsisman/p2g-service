package vip.yazilim.p2g.web.entity.relation;

import lombok.Data;
import vip.yazilim.p2g.web.constant.Constants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = Constants.RELATION_TABLE_PREFIX + "playlist_song")
@Data
public class PlaylistSong implements Serializable {

    @Id
    private String uuid;

    @Column(name = "playlist_uuid")
    private String playlistUuid;

    @Column(name = "song_uuid")
    private String songUuid;

}
