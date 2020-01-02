package vip.yazilim.p2g.web.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import vip.yazilim.p2g.web.constant.Constants;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author mustafaarifsisman - 29.10.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Entity
@Table(name = Constants.TABLE_PREFIX + "user")
@Data
public class User implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String uuid;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(length = 60, nullable = false)
    private String password;

    @Column(name = "role_name", nullable = false)
    private String roleName;

    @Column(name = "online_status")
    private String onlineStatus;

    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "image_url")
    private String imageUrl;

    private String anthem;

    @Column(name = "now_playing")
    private String nowPlaying;

    // Spotify
    @Column(name = "spotify_product_type")
    private String spotifyProductType;

    @Column(name = "spotify_account_id")
    private String spotifyAccountId;

    // Settings
    @Column(name = "show_activity_flag")
    private Boolean showActivityFlag;

    @Column(name = "show_friends_flag")
    private Boolean showFriendsFlag;

    // Details
    @Column(name = "creation_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime creationDate;

}
