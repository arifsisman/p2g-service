package vip.yazilim.p2g.web.entity;

import lombok.Data;
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
    private String uuid;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "display_name", nullable = false)
    private String displayName;

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

}
