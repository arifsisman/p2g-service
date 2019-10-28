package vip.yazilim.p2g.web.entity;

import lombok.Data;
import vip.yazilim.p2g.web.constant.Constants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = Constants.TABLE_PREFIX + "album")
@Data
public class Album implements Serializable {

    @Id
    private String uuid;

    @Column(name = "album_id")
    private String albumId;

    private String uri;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String artists;

    @Column(name = "image_url")
    private String imageUrl;

}
