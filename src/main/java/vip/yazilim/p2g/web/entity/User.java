package vip.yazilim.p2g.web.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import vip.yazilim.p2g.web.constant.Constants;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

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
    private UUID uuid;

    @Column(name = "display_name", nullable = false, length = 64)
    private String displayName;

    @Column(unique = true, nullable = false, length = 64)
    private String email;

    @Column(nullable = false, length = 64)
    private String password;

    @Column(name = "role_name", nullable = false, length = 20)
    private String roleName;

    @Column(name = "online_status", length = 20)
    private String onlineStatus;

    @Column(name = "country_code", length = 3)
    private String countryCode;

    @Column(name = "image_url", length = 128)
    private String imageUrl;

    private String anthem;

    // Spotify
    @Column(name = "spotify_product_type", length = 20)
    private String spotifyProductType;

    @Column(name = "spotify_account_id", length = 64)
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
