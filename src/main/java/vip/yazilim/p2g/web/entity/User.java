package vip.yazilim.p2g.web.entity;

import lombok.Data;
import org.joda.time.LocalDateTime;
import vip.yazilim.p2g.web.constant.Constants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Entity
@Table(name = Constants.TABLE_PREFIX + "user")
@Data
public class User implements Serializable {

    @Id
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private String id;

    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @Column(unique = true, nullable = false, length = 64)
    private String email;

    @Column(name = "role", nullable = false, length = 16)
    private String role;

    @Column(name = "online_status", length = 16)
    private String onlineStatus;

    @Column(name = "country_code", length = 4)
    private String countryCode;

    @Column(name = "image_url", length = 128)
    private String imageUrl;

    @Column(length = 64)
    private String anthem;

    @Column(name = "spotify_product_type", length = 16)
    private String spotifyProductType;

    @Column(name = "show_activity_flag")
    private Boolean showActivityFlag;

    @Column(name = "show_friends_flag")
    private Boolean showFriendsFlag;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

}
