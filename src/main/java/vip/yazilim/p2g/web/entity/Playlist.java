package vip.yazilim.p2g.web.entity;

import lombok.Data;
import vip.yazilim.p2g.web.constant.Constants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = Constants.TABLE_PREFIX + "playlist")
@Data
public class Playlist implements Serializable {

    @Id
    private String uuid;

    @Column(name = "playlist_id")
    private String playlistId;

    private String uri;

    @Column(nullable = false)
    private String name;

    @Column(name = "image_url")
    private String imageUrl;

}
